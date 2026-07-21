package gov.lawenforcement.incident.service;

import gov.lawenforcement.incident.dto.CaseSearchResult;
import gov.lawenforcement.incident.entity.*;
import gov.lawenforcement.incident.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseSearchServiceTest {

    @Mock
    private CaseMasterRepository caseMasterRepository;
    @Mock
    private DistrictRepository districtRepository;
    @Mock
    private CaseStatusMasterRepository caseStatusMasterRepository;
    @Mock
    private CrimeHeadRepository crimeHeadRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private AccusedRepository accusedRepository;
    @Mock
    private VictimRepository victimRepository;
    @Mock
    private ComplainantDetailsRepository complainantDetailsRepository;
    @Mock
    private ActSectionAssociationRepository actSectionAssociationRepository;

    @InjectMocks
    private CaseSearchService caseSearchService;

    private CaseMaster createCase(Integer id, Integer statusId, Integer stationId, Integer crimeHeadId) {
        CaseMaster cm = new CaseMaster();
        cm.setCaseMasterId(id);
        cm.setCrimeNo("CR-" + id);
        cm.setCaseNo("C" + id);
        cm.setCrimeRegisteredDate(LocalDate.now());
        cm.setBriefFacts("Facts for case " + id);
        cm.setCaseStatusId(statusId);
        cm.setPoliceStationId(stationId);
        cm.setCrimeMajorHeadId(crimeHeadId);
        return cm;
    }

    @Test
    void search_withBriefFacts_usesSpecification() {
        PageRequest pageable = PageRequest.of(0, 20);
        List<CaseMaster> caseList = List.of(createCase(1, 1, 1, 1));
        Page<CaseMaster> casePage = new PageImpl<>(caseList);

        when(caseMasterRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(casePage);
        when(caseStatusMasterRepository.findAll()).thenReturn(List.of());
        when(crimeHeadRepository.findAll()).thenReturn(List.of());
        when(unitRepository.findAll()).thenReturn(List.of());
        when(districtRepository.findAll()).thenReturn(List.of());

        Page<CaseSearchResult> result = caseSearchService.search(
                null, null, null, null, "theft", null, null, pageable);

        assertNotNull(result);
        verify(caseMasterRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void search_withoutBriefFacts_usesNativeSearch() {
        PageRequest pageable = PageRequest.of(0, 20);
        List<CaseMaster> caseList = List.of(createCase(1, 1, 1, 1));
        Page<CaseMaster> casePage = new PageImpl<>(caseList);

        when(caseMasterRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(casePage);
        when(caseStatusMasterRepository.findAll()).thenReturn(List.of());
        when(crimeHeadRepository.findAll()).thenReturn(List.of());
        when(unitRepository.findAll()).thenReturn(List.of());
        when(districtRepository.findAll()).thenReturn(List.of());

        Page<CaseSearchResult> result = caseSearchService.search(
                "Central", 1, 100, "CR-001", null, null, null, pageable);

        assertNotNull(result);
        verify(caseMasterRepository).search("Central", 1, 100, "CR-001", null, null, pageable);
    }

    @Test
    void search_enrichesResultsWithLookups() {
        PageRequest pageable = PageRequest.of(0, 20);
        CaseMaster cm = createCase(1, 1, 10, 100);
        Page<CaseMaster> casePage = new PageImpl<>(List.of(cm));

        when(caseMasterRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(casePage);

        CaseStatusMaster status = new CaseStatusMaster();
        status.setCaseStatusId(1);
        status.setCaseStatusName("Open");
        when(caseStatusMasterRepository.findAll()).thenReturn(List.of(status));

        CrimeHead crimeHead = new CrimeHead();
        crimeHead.setCrimeHeadId(100);
        crimeHead.setCrimeGroupName("Theft");
        when(crimeHeadRepository.findAll()).thenReturn(List.of(crimeHead));

        Unit unit = new Unit();
        unit.setUnitId(10);
        unit.setUnitName("Central Police Station");
        unit.setDistrictId(5);
        when(unitRepository.findAll()).thenReturn(List.of(unit));
        when(unitRepository.findById(10)).thenReturn(Optional.of(unit));

        District district = new District();
        district.setDistrictId(5);
        district.setDistrictName("Central District");
        when(districtRepository.findAll()).thenReturn(List.of(district));

        when(accusedRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(victimRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(complainantDetailsRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(actSectionAssociationRepository.findByCaseMasterId(1)).thenReturn(List.of());

        Page<CaseSearchResult> result = caseSearchService.search(
                null, null, null, null, null, null, null, pageable);

        assertEquals(1, result.getContent().size());
        CaseSearchResult r = result.getContent().getFirst();
        assertEquals("CR-1", r.getCrimeNo());
        assertEquals("Open", r.getStatusName());
        assertEquals("Theft", r.getCrimeHeadName());
        assertEquals("Central Police Station", r.getPoliceStationName());
        assertEquals(5, r.getDistrictId());
        assertEquals("Central District", r.getDistrictName());
    }

    @Test
    void search_emptyResult_returnsEmptyPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<CaseMaster> emptyPage = new PageImpl<>(List.of());

        when(caseMasterRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(emptyPage);
        when(caseStatusMasterRepository.findAll()).thenReturn(List.of());
        when(crimeHeadRepository.findAll()).thenReturn(List.of());
        when(unitRepository.findAll()).thenReturn(List.of());
        when(districtRepository.findAll()).thenReturn(List.of());

        Page<CaseSearchResult> result = caseSearchService.search(
                "Unknown", null, null, null, null, null, null, pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }
}
