package gov.lawenforcement.incident.service;

import gov.lawenforcement.common.audit.Auditable;
import gov.lawenforcement.common.exception.ResourceNotFoundException;
import gov.lawenforcement.incident.dto.*;
import gov.lawenforcement.incident.entity.CaseMaster;
import gov.lawenforcement.incident.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", id));
    }

    public CaseDetailResponse getCaseDetail(Integer id) {
        CaseMaster caseMaster = getById(id);
        return CaseDetailResponse.builder()
                .caseInfo(caseMaster)
                .complainants(complainantDetailsRepository.findByCaseMasterId(id))
                .victims(victimRepository.findByCaseMasterId(id))
                .accused(accusedRepository.findByCaseMasterId(id))
                .arrests(arrestSurrenderRepository.findByCaseMasterId(id))
                .actSections(actSectionAssociationRepository.findByCaseMasterId(id))
                .chargesheets(chargesheetDetailsRepository.findByCaseMasterId(id))
                .occurrenceTime(invOccuranceTimeRepository.findByCaseMasterId(id).orElse(null))
                .build();
    }

    public List<InvolvementDto> getInvolvements(Integer caseId) {
        List<InvolvementDto> involvements = new ArrayList<>();

        complainantDetailsRepository.findByCaseMasterId(caseId).forEach(c ->
                involvements.add(InvolvementDto.builder()
                        .type("COMPLAINANT")
                        .name(c.getComplainantName())
                        .age(c.getAgeYear())
                        .genderId(c.getGenderId())
                        .build())
        );

        victimRepository.findByCaseMasterId(caseId).forEach(v ->
                involvements.add(InvolvementDto.builder()
                        .type("VICTIM")
                        .name(v.getVictimName())
                        .age(v.getAgeYear())
                        .genderId(v.getGenderId())
                        .build())
        );

        accusedRepository.findByCaseMasterId(caseId).forEach(a ->
                involvements.add(InvolvementDto.builder()
                        .type("ACCUSED")
                        .name(a.getAccusedName())
                        .age(a.getAgeYear())
                        .genderId(a.getGenderId())
                        .personId(a.getPersonId())
                        .build())
        );

        return involvements;
    }

    public CaseStatsResponse getStats() {
        List<CaseMaster> allCases = caseMasterRepository.findAll();
        return CaseStatsResponse.builder()
                .totalCases(allCases.size())
                .openCases(allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 1).count())
                .underInvestigation(allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 2).count())
                .chargeSheeted(allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() == 3).count())
                .closed(allCases.stream().filter(c -> c.getCaseStatusId() != null && c.getCaseStatusId() >= 4).count())
                .build();
    }

    public List<UnitStatsDto> getDistrictStats() {
        List<CaseMaster> allCases = caseMasterRepository.findAll();
        Map<Integer, Long> stationCounts = allCases.stream()
                .filter(c -> c.getPoliceStationId() != null)
                .collect(Collectors.groupingBy(CaseMaster::getPoliceStationId, Collectors.counting()));
        return stationCounts.entrySet().stream()
                .map(e -> UnitStatsDto.builder().unitId(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    public List<CrimeHeadStatsDto> getCrimeHeadStats() {
        List<CaseMaster> allCases = caseMasterRepository.findAll();
        Map<Integer, Long> headCounts = allCases.stream()
                .filter(c -> c.getCrimeMajorHeadId() != null)
                .collect(Collectors.groupingBy(CaseMaster::getCrimeMajorHeadId, Collectors.counting()));
        return headCounts.entrySet().stream()
                .map(e -> CrimeHeadStatsDto.builder().crimeHeadId(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    @Auditable(action = "CREATE", entityType = "CASE", description = "Create new case")
    public CaseMaster createCase(CaseMaster caseMaster) {
        LocalDate now = LocalDate.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yy/MM"));

        long crimeCount = caseMasterRepository.count();
        caseMaster.setCrimeNo(String.format("CR/%s/%04d", datePart, crimeCount + 1));

        long caseCount = caseMasterRepository.countByCaseNoStartingWith("CS/" + datePart);
        caseMaster.setCaseNo(String.format("CS/%s/%04d", datePart, caseCount + 1));

        return caseMasterRepository.save(caseMaster);
    }

    @Auditable(action = "UPDATE", entityType = "CASE", description = "Update case details")
    public CaseMaster updateCase(Integer id, CaseMaster updates) {
        CaseMaster existing = getById(id);
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

    @Auditable(action = "UPDATE_STATUS", entityType = "CASE", description = "Update case status")
    public CaseMaster updateCaseStatus(Integer id, Integer statusId) {
        CaseMaster existing = getById(id);
        existing.setCaseStatusId(statusId);
        return caseMasterRepository.save(existing);
    }

    @Auditable(action = "DELETE", entityType = "CASE", description = "Delete case")
    public void deleteCase(Integer id) {
        CaseMaster existing = getById(id);
        caseMasterRepository.delete(existing);
    }
}
