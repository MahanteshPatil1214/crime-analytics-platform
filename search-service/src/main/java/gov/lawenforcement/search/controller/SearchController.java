package gov.lawenforcement.search.controller;

import gov.lawenforcement.search.dto.AutocompleteItem;
import gov.lawenforcement.search.dto.IndexResultResponse;
import gov.lawenforcement.search.dto.SearchStatsResponse;
import gov.lawenforcement.search.service.IndexingService;
import gov.lawenforcement.search.service.SearchService;
import gov.lawenforcement.search.repository.CaseSearchRepository;
import gov.lawenforcement.search.repository.PersonSearchRepository;
import gov.lawenforcement.search.repository.FinancialSearchRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @PreAuthorize("hasRole('ADMIN')")
    @Timed(value = "search.reindex", description = "Time to reindex all documents")
    public ResponseEntity<IndexResultResponse> reindex() {
        return ResponseEntity.ok(indexingService.indexAll());
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
    public ResponseEntity<List<AutocompleteItem>> autocomplete(@RequestParam String q) {
        return ResponseEntity.ok(searchService.autocomplete(q));
    }

    @GetMapping("/stats")
    public ResponseEntity<SearchStatsResponse> stats() {
        return ResponseEntity.ok(SearchStatsResponse.builder()
                .casesCount(caseRepo.count())
                .personsCount(personRepo.count())
                .financialCount(financialRepo.count())
                .build());
    }
}
