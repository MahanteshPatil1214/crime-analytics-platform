package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.dto.CaseSearchResult;
import gov.lawenforcement.incident.service.CaseMasterService;
import gov.lawenforcement.incident.service.CaseSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseMasterControllerTest {

    @Mock
    private CaseMasterService caseMasterService;
    @Mock
    private CaseSearchService caseSearchService;

    @InjectMocks
    private CaseMasterController controller;

    @Test
    void search_returnsPage() {
        CaseSearchResult r = new CaseSearchResult();
        r.setCrimeNo("CR-001");
        Page<CaseSearchResult> page = new PageImpl<>(List.of(r));
        when(caseSearchService.search(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(page);

        ResponseEntity<Page<CaseSearchResult>> response = controller.search(
                "Central", 1, 100, "CR-001", null, null, null, 0, 20);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("CR-001", response.getBody().getContent().getFirst().getCrimeNo());
        verify(caseSearchService).search("Central", 1, 100, "CR-001", null, null, null, PageRequest.of(0, 20));
    }

    @Test
    void getCaseDetail_returnsDetail() {
        Map<String, Object> detail = Map.of("case", "data");
        when(caseMasterService.getCaseDetail(1)).thenReturn(detail);

        ResponseEntity<Map<String, Object>> response = controller.getCaseDetail(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("data", response.getBody().get("case"));
    }

    @Test
    void getInvolvements_returnsList() {
        List<Map<String, Object>> involvements = List.of(Map.of("type", "ACCUSED"));
        when(caseMasterService.getInvolvements(1)).thenReturn(involvements);

        ResponseEntity<List<Map<String, Object>>> response = controller.getInvolvements(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getStats_returnsStats() {
        Map<String, Object> stats = Map.of("totalCases", 100);
        when(caseMasterService.getStats()).thenReturn(stats);

        ResponseEntity<Map<String, Object>> response = controller.getStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(100, response.getBody().get("totalCases"));
    }

    @Test
    void getDistrictStats_returnsStats() {
        List<Map<String, Object>> stats = List.of(Map.of("unitId", 1, "count", 10L));
        when(caseMasterService.getDistrictStats()).thenReturn(stats);

        ResponseEntity<List<Map<String, Object>>> response = controller.getDistrictStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCrimeHeadStats_returnsStats() {
        List<Map<String, Object>> stats = List.of(Map.of("crimeHeadId", 100, "count", 5L));
        when(caseMasterService.getCrimeHeadStats()).thenReturn(stats);

        ResponseEntity<List<Map<String, Object>>> response = controller.getCrimeHeadStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
