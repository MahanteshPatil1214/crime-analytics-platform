package gov.lawenforcement.incident.controller;

import gov.lawenforcement.incident.dto.CaseSearchResult;
import gov.lawenforcement.incident.service.CaseMasterService;
import gov.lawenforcement.incident.service.CaseSearchService;
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
public class CaseMasterController {

    private final CaseMasterService caseMasterService;
    private final CaseSearchService caseSearchService;

    @GetMapping("/search")
    public ResponseEntity<Page<CaseSearchResult>> search(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Integer statusId,
            @RequestParam(required = false) Integer crimeHeadId,
            @RequestParam(required = false) String crimeNo,
            @RequestParam(required = false) String briefFacts,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CaseSearchResult> result = caseSearchService.search(district, statusId, crimeHeadId, crimeNo, briefFacts, startDate, endDate, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCaseDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(caseMasterService.getCaseDetail(id));
    }

    @GetMapping("/{id}/involvements")
    public ResponseEntity<List<Map<String, Object>>> getInvolvements(@PathVariable Integer id) {
        return ResponseEntity.ok(caseMasterService.getInvolvements(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(caseMasterService.getStats());
    }

    @GetMapping("/stats/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistrictStats() {
        return ResponseEntity.ok(caseMasterService.getDistrictStats());
    }

    @GetMapping("/stats/crime-heads")
    public ResponseEntity<List<Map<String, Object>>> getCrimeHeadStats() {
        return ResponseEntity.ok(caseMasterService.getCrimeHeadStats());
    }
}
