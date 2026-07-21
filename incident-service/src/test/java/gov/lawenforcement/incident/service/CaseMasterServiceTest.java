package gov.lawenforcement.incident.service;

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

import java.math.BigDecimal;
import java.time.LocalDate;
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
        cm.setCrimeRegisteredDate(LocalDate.now());
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
        verify(caseMasterRepository).search("District", 1, 1, "CR-001", null, null, pageable);
    }

    @Test
    void search_withNullFilters() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<CaseMaster> expectedPage = new PageImpl<>(List.of());
        when(caseMasterRepository.search(any(), any(), any(), any(), any(), any(), any())).thenReturn(expectedPage);

        Page<CaseMaster> result = caseMasterService.search(null, null, null, null, pageable);

        assertTrue(result.getContent().isEmpty());
        verify(caseMasterRepository).search(null, null, null, null, null, null, pageable);
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

        assertThrows(NoSuchElementException.class, () -> caseMasterService.getById(999));
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

        Map<String, Object> detail = caseMasterService.getCaseDetail(1);

        assertEquals(8, detail.size());
        assertTrue(detail.containsKey("case"));
        assertTrue(detail.containsKey("complainants"));
        assertTrue(detail.containsKey("victims"));
        assertTrue(detail.containsKey("accused"));
        assertTrue(detail.containsKey("arrests"));
        assertTrue(detail.containsKey("actSections"));
        assertTrue(detail.containsKey("chargesheets"));
        assertTrue(detail.containsKey("occurrenceTime"));
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

        List<Map<String, Object>> involvements = caseMasterService.getInvolvements(1);

        assertEquals(3, involvements.size());
        assertEquals("COMPLAINANT", involvements.get(0).get("type"));
        assertEquals("Victim Person", involvements.get(1).get("name"));
        assertEquals("ACCUSED", involvements.get(2).get("type"));
        assertEquals("P123", involvements.get(2).get("personId"));
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

        Map<String, Object> stats = caseMasterService.getStats();

        assertEquals(5, stats.get("totalCases"));
        assertEquals(1L, stats.get("openCases"));
        assertEquals(2L, stats.get("underInvestigation"));
        assertEquals(1L, stats.get("chargeSheeted"));
        assertEquals(1L, stats.get("closed"));
    }

    @Test
    void getStats_nullStatus_ignored() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, null, 1, 1),
                createCase(2, 1, 1, 1)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        Map<String, Object> stats = caseMasterService.getStats();

        assertEquals(2, stats.get("totalCases"));
        assertEquals(1L, stats.get("openCases"));
        assertEquals(0L, stats.get("underInvestigation"));
    }

    @Test
    void getDistrictStats_groupsByStation() {
        List<CaseMaster> cases = Arrays.asList(
                createCase(1, 1, 10, 1),
                createCase(2, 1, 10, 1),
                createCase(3, 1, 20, 1)
        );
        when(caseMasterRepository.findAll()).thenReturn(cases);

        List<Map<String, Object>> stats = caseMasterService.getDistrictStats();

        assertEquals(2, stats.size());
        Map<Integer, Long> countMap = new HashMap<>();
        for (Map<String, Object> s : stats) {
            countMap.put((Integer) s.get("unitId"), (Long) s.get("count"));
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

        List<Map<String, Object>> stats = caseMasterService.getCrimeHeadStats();

        assertEquals(2, stats.size());
        Map<Integer, Long> countMap = new HashMap<>();
        for (Map<String, Object> s : stats) {
            countMap.put((Integer) s.get("crimeHeadId"), (Long) s.get("count"));
        }
        assertEquals(2L, countMap.get(100));
        assertEquals(1L, countMap.get(200));
    }
}
