package gov.lawenforcement.search.controller;

import gov.lawenforcement.search.service.IndexingService;
import gov.lawenforcement.search.service.SearchService;
import gov.lawenforcement.search.repository.CaseSearchRepository;
import gov.lawenforcement.search.repository.PersonSearchRepository;
import gov.lawenforcement.search.repository.FinancialSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final IndexingService indexingService;
    private final SearchService searchService;
    private final CaseSearchRepository caseRepo;
    private final PersonSearchRepository personRepo;
    private final FinancialSearchRepository financialRepo;

    @PostMapping("/reindex")
    public ResponseEntity<Map<String, Object>> reindex() {
        Map<String, Object> result = indexingService.indexAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cases")
    public ResponseEntity<Map<String, Object>> searchCases(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String crimeHead) {
        return ResponseEntity.ok(searchService.searchCases(q, district, status, crimeHead));
    }

    @GetMapping("/persons")
    public ResponseEntity<Map<String, Object>> searchPersons(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(searchService.searchPersons(q, type));
    }

    @GetMapping("/financial")
    public ResponseEntity<Map<String, Object>> searchFinancial(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean flagged) {
        return ResponseEntity.ok(searchService.searchFinancial(q, flagged));
    }

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(@RequestParam String q) {
        return ResponseEntity.ok(searchService.globalSearch(q));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String q) {
        return ResponseEntity.ok(searchService.autocomplete(q));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("casesCount", caseRepo.count());
        stats.put("personsCount", personRepo.count());
        stats.put("financialCount", financialRepo.count());
        return ResponseEntity.ok(stats);
    }
}
