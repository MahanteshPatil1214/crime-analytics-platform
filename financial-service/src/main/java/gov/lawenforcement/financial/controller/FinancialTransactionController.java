package gov.lawenforcement.financial.controller;

import gov.lawenforcement.financial.entity.FinancialTransaction;
import gov.lawenforcement.financial.service.FinancialTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/financial")
@RequiredArgsConstructor
public class FinancialTransactionController {

    private final FinancialTransactionService service;

    @GetMapping("/{id}")
    public ResponseEntity<FinancialTransaction> getTransaction(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getTransaction(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FinancialTransaction>> search(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) FinancialTransaction.TransactionType type,
            @RequestParam(required = false) Boolean flagged,
            @RequestParam(required = false) Double minRisk,
            @RequestParam(required = false) Double maxRisk,
            Pageable pageable) {
        return ResponseEntity.ok(service.searchTransactions(accountId, type, flagged, minRisk, maxRisk, pageable));
    }

    @GetMapping("/flagged")
    public ResponseEntity<Page<FinancialTransaction>> getFlaggedTransactions(Pageable pageable) {
        return ResponseEntity.ok(service.getFlaggedTransactions(pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getTransactionStats());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<FinancialTransaction>> getByCase(@PathVariable Integer caseId) {
        return ResponseEntity.ok(service.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<FinancialTransaction> createTransaction(@RequestBody FinancialTransaction transaction) {
        return ResponseEntity.ok(service.createTransaction(transaction));
    }

    @PostMapping("/{id}/flag")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<FinancialTransaction> flagTransaction(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.flagTransaction(id, body.get("reason")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<FinancialTransaction> updateTransaction(
            @PathVariable UUID id,
            @RequestBody FinancialTransaction transaction) {
        return ResponseEntity.ok(service.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICER')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        service.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
