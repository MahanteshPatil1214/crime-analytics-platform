package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.dto.CaseSearchResult;
import gov.lawenforcement.incident.entity.*;
import gov.lawenforcement.incident.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaseSearchService {

    private final CaseMasterRepository caseMasterRepository;
    private final DistrictRepository districtRepository;
    private final CaseStatusMasterRepository caseStatusMasterRepository;
    private final CrimeHeadRepository crimeHeadRepository;
    private final UnitRepository unitRepository;
    private final AccusedRepository accusedRepository;
    private final VictimRepository victimRepository;
    private final ComplainantDetailsRepository complainantDetailsRepository;
    private final ActSectionAssociationRepository actSectionAssociationRepository;

    public Page<CaseSearchResult> search(String district, Integer statusId, Integer crimeHeadId, String crimeNo, String briefFacts, String startDate, String endDate, Pageable pageable) {
        Page<CaseMaster> casePage;

        if (briefFacts != null && !briefFacts.isBlank()) {
            casePage = caseMasterRepository.findAll(
                    CaseSpecification.filterBy(crimeNo, briefFacts, statusId, startDate, endDate), pageable);
        } else {
            casePage = caseMasterRepository.search(district, statusId, crimeHeadId, crimeNo, startDate, endDate, pageable);
        }

        List<CaseSearchResult> results = enrichResults(casePage.getContent());
        return new PageImpl<>(results, pageable, casePage.getTotalElements());
    }

    private List<CaseSearchResult> enrichResults(List<CaseMaster> cases) {
        Map<Integer, String> districtCache = new HashMap<>();
        Map<Integer, String> statusCache = new HashMap<>();
        Map<Integer, String> crimeHeadCache = new HashMap<>();
        Map<Integer, String> unitCache = new HashMap<>();

        for (CaseStatusMaster s : caseStatusMasterRepository.findAll()) {
            statusCache.put(s.getCaseStatusId(), s.getCaseStatusName());
        }
        for (CrimeHead h : crimeHeadRepository.findAll()) {
            crimeHeadCache.put(h.getCrimeHeadId(), h.getCrimeGroupName());
        }
        for (Unit u : unitRepository.findAll()) {
            unitCache.put(u.getUnitId(), u.getUnitName());
        }
        for (District d : districtRepository.findAll()) {
            districtCache.put(d.getDistrictId(), d.getDistrictName());
        }

        Set<Integer> caseIds = cases.stream().map(CaseMaster::getCaseMasterId).collect(Collectors.toSet());

        Map<Integer, List<String>> accusedMap = new HashMap<>();
        Map<Integer, List<String>> victimMap = new HashMap<>();
        Map<Integer, List<String>> complainantMap = new HashMap<>();
        Map<Integer, List<String>> actSectionMap = new HashMap<>();

        for (Integer cid : caseIds) {
            accusedMap.put(cid, accusedRepository.findByCaseMasterId(cid).stream()
                    .map(Accused::getAccusedName).collect(Collectors.toList()));
            victimMap.put(cid, victimRepository.findByCaseMasterId(cid).stream()
                    .map(Victim::getVictimName).collect(Collectors.toList()));
            complainantMap.put(cid, complainantDetailsRepository.findByCaseMasterId(cid).stream()
                    .map(ComplainantDetails::getComplainantName).collect(Collectors.toList()));
            actSectionMap.put(cid, actSectionAssociationRepository.findByCaseMasterId(cid).stream()
                    .map(a -> a.getActCode() + "/" + a.getSectionCode()).collect(Collectors.toList()));
        }

        List<CaseSearchResult> results = new ArrayList<>();
        for (CaseMaster cm : cases) {
            CaseSearchResult r = new CaseSearchResult();
            r.setCaseMasterId(cm.getCaseMasterId());
            r.setCrimeNo(cm.getCrimeNo());
            r.setCaseNo(cm.getCaseNo());
            r.setCrimeRegisteredDate(cm.getCrimeRegisteredDate());
            r.setBriefFacts(cm.getBriefFacts());
            r.setLatitude(cm.getLatitude());
            r.setLongitude(cm.getLongitude());
            r.setCaseStatusId(cm.getCaseStatusId());
            r.setStatusName(cm.getCaseStatusId() != null ? statusCache.get(cm.getCaseStatusId()) : null);
            r.setCrimeMajorHeadId(cm.getCrimeMajorHeadId());
            r.setCrimeHeadName(cm.getCrimeMajorHeadId() != null ? crimeHeadCache.get(cm.getCrimeMajorHeadId()) : null);
            r.setPoliceStationId(cm.getPoliceStationId());
            r.setPoliceStationName(cm.getPoliceStationId() != null ? unitCache.get(cm.getPoliceStationId()) : null);

            if (cm.getPoliceStationId() != null) {
                unitRepository.findById(cm.getPoliceStationId()).ifPresent(u -> {
                    r.setDistrictId(u.getDistrictId());
                    r.setDistrictName(u.getDistrictId() != null ? districtCache.get(u.getDistrictId()) : null);
                });
            }

            r.setAccusedNames(accusedMap.getOrDefault(cm.getCaseMasterId(), Collections.emptyList()));
            r.setVictimNames(victimMap.getOrDefault(cm.getCaseMasterId(), Collections.emptyList()));
            r.setComplainantNames(complainantMap.getOrDefault(cm.getCaseMasterId(), Collections.emptyList()));
            r.setActSections(actSectionMap.getOrDefault(cm.getCaseMasterId(), Collections.emptyList()));

            results.add(r);
        }
        return results;
    }
}
