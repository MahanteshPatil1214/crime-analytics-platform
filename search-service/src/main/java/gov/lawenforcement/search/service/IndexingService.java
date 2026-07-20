package gov.lawenforcement.search.service;

import gov.lawenforcement.search.document.CaseDocument;
import gov.lawenforcement.search.document.FinancialDocument;
import gov.lawenforcement.search.document.PersonDocument;
import gov.lawenforcement.search.repository.CaseSearchRepository;
import gov.lawenforcement.search.repository.FinancialSearchRepository;
import gov.lawenforcement.search.repository.PersonSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingService {

    private final JdbcTemplate jdbc;
    private final CaseSearchRepository caseRepo;
    private final PersonSearchRepository personRepo;
    private final FinancialSearchRepository financialRepo;

    public Map<String, Object> indexAll() {
        Map<String, Object> result = new LinkedHashMap<>();
        long cases = indexAllCases();
        long persons = indexAllPersons();
        long financial = indexAllFinancial();
        result.put("casesIndexed", cases);
        result.put("personsIndexed", persons);
        result.put("financialIndexed", financial);
        return result;
    }

    public long indexAllCases() {
        log.info("Starting case indexing...");
        caseRepo.deleteAll();

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT c.case_master_id, c.crime_no, c.case_no,
                   TO_CHAR(c.crime_registered_date, 'YYYY-MM-DD') AS crime_date,
                   c.brief_facts, c.latitude, c.longitude,
                   d.district_name, ch.crime_group_name,
                   csm.case_status_name,
                   u.unit_name AS police_station_name
            FROM case_master c
            LEFT JOIN unit u ON c.police_station_id = u.unit_id
            LEFT JOIN district d ON u.district_id = d.district_id
            LEFT JOIN crime_head ch ON c.crime_major_head_id = ch.crime_head_id
            LEFT JOIN case_status_master csm ON c.case_status_id = csm.case_status_id
            ORDER BY c.case_master_id
            """);

        List<CaseDocument> docs = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            CaseDocument doc = new CaseDocument();
            Integer caseId = ((Number) row.get("case_master_id")).intValue();
            String crimeNo = (String) row.get("crime_no");

            doc.setCaseMasterId(caseId);
            doc.setCrimeNo(crimeNo);
            doc.setCaseNo((String) row.get("case_no"));
            doc.setCrimeRegisteredDate((String) row.get("crime_date"));
            doc.setBriefFacts((String) row.get("brief_facts"));
            doc.setLatitude(row.get("latitude") != null ? ((Number) row.get("latitude")).doubleValue() : null);
            doc.setLongitude(row.get("longitude") != null ? ((Number) row.get("longitude")).doubleValue() : null);
            doc.setDistrictName((String) row.get("district_name"));
            doc.setCrimeHeadName((String) row.get("crime_group_name"));
            doc.setStatusName((String) row.get("case_status_name"));
            doc.setPoliceStationName((String) row.get("police_station_name"));

            List<String> accused = jdbc.queryForList(
                "SELECT accused_name FROM accused WHERE case_master_id = ?", caseId)
                .stream().map(m -> (String) m.get("accused_name")).collect(Collectors.toList());
            doc.setAccusedNames(accused);

            List<String> victims = jdbc.queryForList(
                "SELECT victim_name FROM victim WHERE case_master_id = ?", caseId)
                .stream().map(m -> (String) m.get("victim_name")).collect(Collectors.toList());
            doc.setVictimNames(victims);

            List<String> complainants = jdbc.queryForList(
                "SELECT complainant_name FROM complainant_details WHERE case_master_id = ?", caseId)
                .stream().map(m -> (String) m.get("complainant_name")).collect(Collectors.toList());
            doc.setComplainantNames(complainants);

            List<String> acts = jdbc.queryForList(
                "SELECT act_code || ' ' || section_code AS act_section FROM act_section_association WHERE case_master_id = ?", caseId)
                .stream().map(m -> (String) m.get("act_section")).collect(Collectors.toList());
            doc.setActSections(acts);

            StringBuilder text = new StringBuilder();
            if (doc.getBriefFacts() != null) text.append(doc.getBriefFacts()).append(" ");
            if (doc.getDistrictName() != null) text.append(doc.getDistrictName()).append(" ");
            if (doc.getCrimeHeadName() != null) text.append(doc.getCrimeHeadName()).append(" ");
            accused.forEach(a -> text.append(a).append(" "));
            victims.forEach(v -> text.append(v).append(" "));
            complainants.forEach(c -> text.append(c).append(" "));
            acts.forEach(a -> text.append(a).append(" "));
            doc.setSearchableText(text.toString().trim());

            docs.add(doc);
        }

        caseRepo.saveAll(docs);
        log.info("Indexed {} cases", docs.size());
        return docs.size();
    }

    public long indexAllPersons() {
        log.info("Starting person indexing...");
        personRepo.deleteAll();

        List<PersonDocument> docs = new ArrayList<>();

        List<Map<String, Object>> accused = jdbc.queryForList(
            "SELECT a.accused_master_id, a.accused_name, a.age_year, a.gender_id, a.case_master_id, c.crime_no " +
            "FROM accused a JOIN case_master c ON a.case_master_id = c.case_master_id");
        Map<String, List<Map<String, Object>>> byPersonId = new LinkedHashMap<>();
        for (Map<String, Object> row : accused) {
            String key = "ACCUSED_" + row.get("accused_master_id");
            byPersonId.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (var entry : byPersonId.entrySet()) {
            Map<String, Object> first = entry.getValue().get(0);
            PersonDocument doc = new PersonDocument();
            doc.setId(entry.getKey());
            doc.setDbId(((Number) first.get("accused_master_id")).intValue());
            doc.setName((String) first.get("accused_name"));
            doc.setAge(first.get("age_year") != null ? ((Number) first.get("age_year")).intValue() : null);
            doc.setGender(resolveGender(first.get("gender_id")));
            doc.setPersonType("ACCUSED");
            doc.setCaseCrimeNos(entry.getValue().stream().map(m -> (String) m.get("crime_no")).collect(Collectors.toList()));
            doc.setCaseIds(entry.getValue().stream().map(m -> ((Number) m.get("case_master_id")).intValue()).collect(Collectors.toList()));
            docs.add(doc);
        }

        List<Map<String, Object>> victims = jdbc.queryForList(
            "SELECT v.victim_master_id, v.victim_name, v.age_year, v.gender_id, v.case_master_id, c.crime_no " +
            "FROM victim v JOIN case_master c ON v.case_master_id = c.case_master_id");
        Map<String, List<Map<String, Object>>> byVictimId = new LinkedHashMap<>();
        for (Map<String, Object> row : victims) {
            String key = "VICTIM_" + row.get("victim_master_id");
            byVictimId.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (var entry : byVictimId.entrySet()) {
            Map<String, Object> first = entry.getValue().get(0);
            PersonDocument doc = new PersonDocument();
            doc.setId(entry.getKey());
            doc.setDbId(((Number) first.get("victim_master_id")).intValue());
            doc.setName((String) first.get("victim_name"));
            doc.setAge(first.get("age_year") != null ? ((Number) first.get("age_year")).intValue() : null);
            doc.setGender(resolveGender(first.get("gender_id")));
            doc.setPersonType("VICTIM");
            doc.setCaseCrimeNos(entry.getValue().stream().map(m -> (String) m.get("crime_no")).collect(Collectors.toList()));
            doc.setCaseIds(entry.getValue().stream().map(m -> ((Number) m.get("case_master_id")).intValue()).collect(Collectors.toList()));
            docs.add(doc);
        }

        List<Map<String, Object>> comp = jdbc.queryForList(
            "SELECT cd.complainant_id, cd.complainant_name, cd.age_year, cd.gender_id, cd.case_master_id, c.crime_no " +
            "FROM complainant_details cd JOIN case_master c ON cd.case_master_id = c.case_master_id");
        Map<String, List<Map<String, Object>>> byCompId = new LinkedHashMap<>();
        for (Map<String, Object> row : comp) {
            String key = "COMPLAINANT_" + row.get("complainant_id");
            byCompId.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        for (var entry : byCompId.entrySet()) {
            Map<String, Object> first = entry.getValue().get(0);
            PersonDocument doc = new PersonDocument();
            doc.setId(entry.getKey());
            doc.setDbId(((Number) first.get("complainant_id")).intValue());
            doc.setName((String) first.get("complainant_name"));
            doc.setAge(first.get("age_year") != null ? ((Number) first.get("age_year")).intValue() : null);
            doc.setGender(resolveGender(first.get("gender_id")));
            doc.setPersonType("COMPLAINANT");
            doc.setCaseCrimeNos(entry.getValue().stream().map(m -> (String) m.get("crime_no")).collect(Collectors.toList()));
            doc.setCaseIds(entry.getValue().stream().map(m -> ((Number) m.get("case_master_id")).intValue()).collect(Collectors.toList()));
            docs.add(doc);
        }

        personRepo.saveAll(docs);
        log.info("Indexed {} persons", docs.size());
        return docs.size();
    }

    public long indexAllFinancial() {
        log.info("Starting financial transaction indexing...");
        financialRepo.deleteAll();

        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT ft.id::text AS ft_id, ft.transaction_ref, ft.sender_account_id,
                   ft.recipient_account_id, ft.amount, ft.currency,
                   ft.transaction_date::text AS tx_date, ft.transaction_type,
                   ft.is_flagged, c.crime_no
            FROM financial_transactions ft
            LEFT JOIN case_master c ON ft.related_case_id = c.case_master_id
            ORDER BY ft.transaction_date DESC
            """);

        List<FinancialDocument> docs = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            FinancialDocument doc = new FinancialDocument();
            doc.setId((String) row.get("ft_id"));
            doc.setTransactionRef((String) row.get("transaction_ref"));
            doc.setSenderAccountId((String) row.get("sender_account_id"));
            doc.setRecipientAccountId((String) row.get("recipient_account_id"));
            doc.setAmount(row.get("amount") != null ? ((Number) row.get("amount")).doubleValue() : null);
            doc.setCurrency((String) row.get("currency"));
            doc.setTransactionDate((String) row.get("tx_date"));
            doc.setTransactionType((String) row.get("transaction_type"));
            doc.setFlagged(row.get("is_flagged") != null && Boolean.parseBoolean(row.get("is_flagged").toString()));
            doc.setRelatedCaseCrimeNo((String) row.get("crime_no"));
            docs.add(doc);
        }

        financialRepo.saveAll(docs);
        log.info("Indexed {} financial transactions", docs.size());
        return docs.size();
    }

    private String resolveGender(Object genderId) {
        if (genderId == null) return "UNKNOWN";
        int id = ((Number) genderId).intValue();
        return switch (id) {
            case 1 -> "MALE";
            case 2 -> "FEMALE";
            case 3 -> "OTHER";
            default -> "UNKNOWN";
        };
    }
}
