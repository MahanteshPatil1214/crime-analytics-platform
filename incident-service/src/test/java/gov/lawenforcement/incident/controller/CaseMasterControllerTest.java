package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.dto.*;
import gov.lawenforcement.incident.entity.CaseMaster;
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
    }

    @Test
    void getCaseDetail_returnsDetail() {
        CaseMaster cm = new CaseMaster();
        cm.setCrimeNo("CR-1");
        CaseDetailResponse detail = CaseDetailResponse.builder()
                .caseInfo(cm)
                .complainants(List.of())
                .victims(List.of())
                .accused(List.of())
                .arrests(List.of())
                .actSections(List.of())
                .chargesheets(List.of())
                .occurrenceTime(null)
                .build();
        when(caseMasterService.getCaseDetail(1)).thenReturn(detail);

        ResponseEntity<CaseDetailResponse> response = controller.getCaseDetail(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("CR-1", response.getBody().getCaseInfo().getCrimeNo());
    }

    @Test
    void getInvolvements_returnsList() {
        List<InvolvementDto> involvements = List.of(
                InvolvementDto.builder().type("ACCUSED").name("John").build()
        );
        when(caseMasterService.getInvolvements(1)).thenReturn(involvements);

        ResponseEntity<List<InvolvementDto>> response = controller.getInvolvements(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getStats_returnsStats() {
        CaseStatsResponse stats = CaseStatsResponse.builder()
                .totalCases(100)
                .openCases(10L)
                .underInvestigation(5L)
                .chargeSheeted(3L)
                .closed(2L)
                .build();
        when(caseMasterService.getStats()).thenReturn(stats);

        ResponseEntity<CaseStatsResponse> response = controller.getStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(100, response.getBody().getTotalCases());
    }

    @Test
    void getDistrictStats_returnsStats() {
        List<UnitStatsDto> stats = List.of(
                UnitStatsDto.builder().unitId(1).count(10L).build()
        );
        when(caseMasterService.getDistrictStats()).thenReturn(stats);

        ResponseEntity<List<UnitStatsDto>> response = controller.getDistrictStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCrimeHeadStats_returnsStats() {
        List<CrimeHeadStatsDto> stats = List.of(
                CrimeHeadStatsDto.builder().crimeHeadId(100).count(5L).build()
        );
        when(caseMasterService.getCrimeHeadStats()).thenReturn(stats);

        ResponseEntity<List<CrimeHeadStatsDto>> response = controller.getCrimeHeadStats();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }
}
