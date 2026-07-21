package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.dto.CaseSearchResult;
import gov.lawenforcement.incident.service.CaseMasterService;
import gov.lawenforcement.incident.service.CaseSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Tag(name = "FIR Cases", description = "Endpoints for managing and searching FIR case records")
public class CaseMasterController {

    private final CaseMasterService caseMasterService;
    private final CaseSearchService caseSearchService;

    @GetMapping("/search")
    @Operation(summary = "Search FIR cases", description = "Paginated search with filters for district, status, crime head, crime number, brief facts, and date range")
    public ResponseEntity<Page<CaseSearchResult>> search(
            @Parameter(description = "District name (partial match)") @RequestParam(required = false) String district,
            @Parameter(description = "Case status ID") @RequestParam(required = false) Integer statusId,
            @Parameter(description = "Crime major head ID") @RequestParam(required = false) Integer crimeHeadId,
            @Parameter(description = "Crime number (partial match)") @RequestParam(required = false) String crimeNo,
            @Parameter(description = "Keyword search in brief facts") @RequestParam(required = false) String briefFacts,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(required = false) String endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Page<CaseSearchResult> result = caseSearchService.search(district, statusId, crimeHeadId, crimeNo, briefFacts, startDate, endDate, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get case detail", description = "Returns full case details including complainants, victims, accused, arrests, charge sheets, and act sections")
    public ResponseEntity<Map<String, Object>> getCaseDetail(
            @Parameter(description = "Case master ID") @PathVariable Integer id) {
        return ResponseEntity.ok(caseMasterService.getCaseDetail(id));
    }

    @GetMapping("/{id}/involvements")
    @Operation(summary = "Get case involvements", description = "Lists all persons (complainants, victims, accused) involved in a case")
    public ResponseEntity<List<Map<String, Object>>> getInvolvements(
            @Parameter(description = "Case master ID") @PathVariable Integer id) {
        return ResponseEntity.ok(caseMasterService.getInvolvements(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get case statistics", description = "Aggregated counts: total, open, under investigation, charge sheeted, closed")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(caseMasterService.getStats());
    }

    @GetMapping("/stats/districts")
    @Operation(summary = "Get district-wise stats", description = "Case counts grouped by police station")
    public ResponseEntity<List<Map<String, Object>>> getDistrictStats() {
        return ResponseEntity.ok(caseMasterService.getDistrictStats());
    }

    @GetMapping("/stats/crime-heads")
    @Operation(summary = "Get crime head stats", description = "Case counts grouped by crime major head")
    public ResponseEntity<List<Map<String, Object>>> getCrimeHeadStats() {
        return ResponseEntity.ok(caseMasterService.getCrimeHeadStats());
    }
}
