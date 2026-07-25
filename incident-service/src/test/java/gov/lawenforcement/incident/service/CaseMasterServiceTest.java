package gov.lawenforcement.incident.service;

import gov.lawenforcement.common.exception.ResourceNotFoundException;
import gov.lawenforcement.incident.dto.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseMasterServiceTest {

    @Mock
    private CaseMasterRepository caseMasterRepository;
    @Mock
    private ComplainantDetailsRepository complainantDetailsRepository;
    @Mock
    private VictimRepository victimRepository;
    @Mock
    private AccusedRepository accusedRepository;
    @Mock
    private ArrestSurrenderRepository arrestSurrenderRepository;
    @Mock
    private ActSectionAssociationRepository actSectionAssociationRepository;
    @Mock
    private ChargesheetDetailsRepository chargesheetDetailsRepository;
    @Mock
    private InvOccuranceTimeRepository invOccuranceTimeRepository;

    @InjectMocks
    private CaseMasterService caseMasterService;

    private CaseMaster createCase(Integer id, Integer statusId, Integer stationId, Integer crimeHeadId) {
        CaseMaster cm = new CaseMaster();
        cm.setCaseMasterId(id);
        cm.setCrimeNo("CR-" + id);
        cm.setCaseStatusId(statusId);
        cm.setPoliceStationId(stationId);
        cm.setCrimeMajorHeadId(crimeHeadId);
        return cm;
    }

    @Test
    void search_delegatesToRepository() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<CaseMaster> expectedPage = new PageImpl<>(List.of(createCase(1, 1, 1, 1)));
        when(caseMasterRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(expectedPage);

        Page<CaseMaster> result = caseMasterService.search("District", 1, 1, "CR-001", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("CR-1", result.getContent().getFirst().getCrimeNo());
    }

    @Test
    void getById_found_returnsCase() {
        CaseMaster cm = createCase(1, 1, 1, 1);
        when(caseMasterRepository.findById(1)).thenReturn(Optional.of(cm));

        CaseMaster result = caseMasterService.getById(1);

        assertNotNull(result);
        assertEquals("CR-1", result.getCrimeNo());
    }

    @Test
    void getById_notFound_throwsException() {
        when(caseMasterRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> caseMasterService.getById(999));
    }

    @Test
    void getCaseDetail_returnsFullDetail() {
        CaseMaster cm = createCase(1, 1, 1, 1);
        when(caseMasterRepository.findById(1)).thenReturn(Optional.of(cm));
        when(complainantDetailsRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(victimRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(accusedRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(arrestSurrenderRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(actSectionAssociationRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(chargesheetDetailsRepository.findByCaseMasterId(1)).thenReturn(List.of());
        when(invOccuranceTimeRepository.findByCaseMasterId(1)).thenReturn(Optional.empty());

        CaseDetailResponse detail = caseMasterService.getCaseDetail(1);

        assertNotNull(detail);
        assertEquals("CR-1", detail.getCaseInfo().getCrimeNo());
        assertTrue(detail.getComplainants().isEmpty());
        assertTrue(detail.getVictims().isEmpty());
        assertTrue(detail.getAccused().isEmpty());
    }

    @Test
    void getInvolvements_returnsAllTypes() {
        Accused accused = new Accused();
        accused.setAccusedName("Accused Person");
        accused.setAgeYear(30);
        accused.setGenderId(1);
        accused.setPersonId("P123");

        Victim victim = new Victim();
        victim.setVictimName("Victim Person");
        victim.setAgeYear(25);
        victim.setGenderId(2);

        ComplainantDetails complainant = new ComplainantDetails();
        complainant.setComplainantName("Complainant Person");
        complainant.setAgeYear(40);
        complainant.setGenderId(1);

        when(complainantDetailsRepository.findByCaseMasterId(1)).thenReturn(List.of(complainant));
        when(victimRepository.findByCaseMasterId(1)).thenReturn(List.of(victim));
        when(accusedRepository.findByCaseMasterId(1)).thenReturn(List.of(accused));

        List<InvolvementDto> involvements = caseMasterService.getInvolvements(1);

        assertEquals(3, involvements.size());
        assertEquals("COMPLAINANT", involvements.get(0).getType());
        assertEquals("Victim Person", involvements.get(1).getName());
        assertEquals("ACCUSED", involvements.get(2).getType());
        assertEquals("P123", involvements.get(2).getPersonId());
    }

    @Test
    void getStats_computesCorrectCounts() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, 1, 1, 1),
                createCase(2, 2, 1, 1),
                createCase(3, 2, 1, 1),
                createCase(4, 3, 2, 2),
                createCase(5, 5, 2, 2)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        CaseStatsResponse stats = caseMasterService.getStats();

        assertEquals(5, stats.getTotalCases());
        assertEquals(1L, stats.getOpenCases());
        assertEquals(2L, stats.getUnderInvestigation());
        assertEquals(1L, stats.getChargeSheeted());
        assertEquals(1L, stats.getClosed());
    }

    @Test
    void getStats_nullStatus_ignored() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, null, 1, 1),
                createCase(2, 1, 1, 1)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        CaseStatsResponse stats = caseMasterService.getStats();

        assertEquals(2, stats.getTotalCases());
        assertEquals(1L, stats.getOpenCases());
        assertEquals(0L, stats.getUnderInvestigation());
    }

    @Test
    void getDistrictStats_groupsByStation() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, 1, 10, 1),
                createCase(2, 1, 10, 1),
                createCase(3, 1, 20, 1)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        List<UnitStatsDto> stats = caseMasterService.getDistrictStats();

        assertEquals(2, stats.size());
        Map<Integer, Long> countMap = new HashMap<>();
        for (UnitStatsDto s : stats) {
            countMap.put(s.getUnitId(), s.getCount());
        }
        assertEquals(2L, countMap.get(10));
        assertEquals(1L, countMap.get(20));
    }

    @Test
    void getCrimeHeadStats_groupsByCrimeHead() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, 1, 1, 100),
                createCase(2, 1, 1, 100),
                createCase(3, 1, 2, 200),
                createCase(4, 2, 2, null)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        List<CrimeHeadStatsDto> stats = caseMasterService.getCrimeHeadStats();

        assertEquals(2, stats.size());
        Map<Integer, Long> countMap = new HashMap<>();
        for (CrimeHeadStatsDto s : stats) {
            countMap.put(s.getCrimeHeadId(), s.getCount());
        }
        assertEquals(2L, countMap.get(100));
        assertEquals(1L, countMap.get(200));
    }
}
