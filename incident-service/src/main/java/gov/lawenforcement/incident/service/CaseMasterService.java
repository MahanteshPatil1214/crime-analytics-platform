package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.entity.CaseMaster;
import gov.lawenforcement.incident.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CaseMasterService {

    private final CaseMasterRepository caseMasterRepository;
    private final ComplainantDetailsRepository complainantDetailsRepository;
    private final VictimRepository victimRepository;
    private final AccusedRepository accusedRepository;
    private final ArrestSurrenderRepository arrestSurrenderRepository;
    private final ActSectionAssociationRepository actSectionAssociationRepository;
    private final ChargesheetDetailsRepository chargesheetDetailsRepository;
    private final InvOccuranceTimeRepository invOccuranceTimeRepository;

    public Page<CaseMaster> search(String district, Integer statusId, Integer crimeHeadId, String crimeNo, Pageable pageable) {
        return caseMasterRepository.search(district, statusId, crimeHeadId, crimeNo, null, null, pageable);
    }

    public CaseMaster getById(Integer id) {
        return caseMasterRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Case not found with id: " + id));
    }

    public Map<String, Object> getCaseDetail(Integer id) {
        CaseMaster caseMaster = getById(id);

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("case", caseMaster);
        detail.put("complainants", complainantDetailsRepository.findByCaseMasterId(id));
        detail.put("victims", victimRepository.findByCaseMasterId(id));
        detail.put("accused", accusedRepository.findByCaseMasterId(id));
        detail.put("arrests", arrestSurrenderRepository.findByCaseMasterId(id));
        detail.put("actSections", actSectionAssociationRepository.findByCaseMasterId(id));
        detail.put("chargesheets", chargesheetDetailsRepository.findByCaseMasterId(id));
        detail.put("occurrenceTime", invOccuranceTimeRepository.findByCaseMasterId(id).orElse(null));

        return detail;
    }

    public List<Map<String, Object>> getInvolvements(Integer caseId) {
        List<Map<String, Object>> involvements = new ArrayList<>();

        complainantDetailsRepository.findByCaseMasterId(caseId).forEach(c -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("type", "COMPLAINANT");
            entry.put("name", c.getComplainantName());
            entry.put("age", c.getAgeYear());
            entry.put("genderId", c.getGenderId());
            involvements.add(entry);
        });

        victimRepository.findByCaseMasterId(caseId).forEach(v -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("type", "VICTIM");
            entry.put("name", v.getVictimName());
            entry.put("age", v.getAgeYear());
            entry.put("genderId", v.getGenderId());
            involvements.add(entry);
        });

        accusedRepository.findByCaseMasterId(caseId).forEach(a -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("type", "ACCUSED");
            entry.put("name", a.getAccusedName());
            entry.put("age", a.getAgeYear());
            entry.put("genderId", a.getGenderId());
            entry.put("personId", a.getPersonId());
            involvements.add(entry);
        });

        return involvements;
    }

    public Map<String, Object> getStats() {
        List<CaseMaster> allCases = caseMasterRepository.findAll();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCases", allCases.size());
        stats.put("openCases", allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 1).count());
        stats.put("underInvestigation", allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 2).count());
        stats.put("chargeSheeted", allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 3).count());
        stats.put("closed", allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() >= 4).count());

        return stats;
    }

    public List<Map<String, Object>> getDistrictStats() {
        List<Map<String, Object>> results = new ArrayList<>();
        List<CaseMaster> allCases = caseMasterRepository.findAll();

        Map<Integer, Long> stationCounts = new HashMap<>();
        for (CaseMaster c : allCases) {
            if (c.getPoliceStationId() != null) {
                stationCounts.merge(c.getPoliceStationId(), 1L, Long::sum);
            }
        }

        for (Map.Entry<Integer, Long> entry : stationCounts.entrySet()) {
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("unitId", entry.getKey());
            stat.put("count", entry.getValue());
            results.add(stat);
        }
        return results;
    }

    public List<Map<String, Object>> getCrimeHeadStats() {
        List<Map<String, Object>> results = new ArrayList<>();
        List<CaseMaster> allCases = caseMasterRepository.findAll();

        Map<Integer, Long> headCounts = new HashMap<>();
        for (CaseMaster c : allCases) {
            if (c.getCrimeMajorHeadId() != null) {
                headCounts.merge(c.getCrimeMajorHeadId(), 1L, Long::sum);
            }
        }

        for (Map.Entry<Integer, Long> entry : headCounts.entrySet()) {
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("crimeHeadId", entry.getKey());
            stat.put("count", entry.getValue());
            results.add(stat);
        }
        return results;
    }

    public CaseMaster createCase(CaseMaster caseMaster) {
        return caseMasterRepository.save(caseMaster);
    }

    public CaseMaster updateCase(Integer id, CaseMaster updates) {
        CaseMaster existing = getById(id);
        existing.setCrimeNo(updates.getCrimeNo());
        existing.setCaseNo(updates.getCaseNo());
        existing.setCrimeRegisteredDate(updates.getCrimeRegisteredDate());
        existing.setPolicePersonId(updates.getPolicePersonId());
        existing.setPoliceStationId(updates.getPoliceStationId());
        existing.setCaseCategoryId(updates.getCaseCategoryId());
        existing.setGravityOffenceId(updates.getGravityOffenceId());
        existing.setCrimeMajorHeadId(updates.getCrimeMajorHeadId());
        existing.setCrimeMinorHeadId(updates.getCrimeMinorHeadId());
        existing.setCaseStatusId(updates.getCaseStatusId());
        existing.setCourtId(updates.getCourtId());
        existing.setIncidentFromDate(updates.getIncidentFromDate());
        existing.setIncidentToDate(updates.getIncidentToDate());
        existing.setInfoReceivedPsDate(updates.getInfoReceivedPsDate());
        existing.setLatitude(updates.getLatitude());
        existing.setLongitude(updates.getLongitude());
        existing.setBriefFacts(updates.getBriefFacts());
        return caseMasterRepository.save(existing);
    }

    public CaseMaster updateCaseStatus(Integer id, Integer statusId) {
        CaseMaster existing = getById(id);
        existing.setCaseStatusId(statusId);
        return caseMasterRepository.save(existing);
    }

    public void deleteCase(Integer id) {
        CaseMaster existing = getById(id);
        caseMasterRepository.delete(existing);
    }
}
