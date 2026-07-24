package gov.lawenforcement.financial.service;

import gov.lawenforcement.common.audit.Auditable;
import gov.lawenforcement.common.exception.ResourceNotFoundException;
import gov.lawenforcement.financial.entity.FinancialTransaction;
import gov.lawenforcement.financial.dto.FinancialStatsResponse;
import gov.lawenforcement.financial.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialTransactionService {

    private final FinancialTransactionRepository repository;

    @Transactional(readOnly = true)
    public FinancialTransaction getTransaction(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<FinancialTransaction> searchTransactions(
            String accountId,
            FinancialTransaction.TransactionType type,
            Boolean flagged,
            Double minRisk,
            Double maxRisk,
            Pageable pageable) {
        return repository.search(accountId, type, flagged, minRisk, maxRisk, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FinancialTransaction> getFlaggedTransactions(Pageable pageable) {
        return repository.findByIsFlaggedTrue(pageable);
    }

    @Transactional(readOnly = true)
    public FinancialStatsResponse getTransactionStats() {
        return FinancialStatsResponse.builder()
                .totalTransactions(repository.count())
                .flaggedCount(repository.countByIsFlagged(true))
                .totalAmount(repository.findAll().stream()
                        .map(FinancialTransaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    @Auditable(action = "FLAG", entityType = "FINANCIAL_TRANSACTION", description = "Flag suspicious transaction")
    @Transactional
    public FinancialTransaction flagTransaction(UUID id, String reason) {
        FinancialTransaction tx = getTransaction(id);
        tx.setIsFlagged(true);
        tx.setFlagReason(reason);
        log.info("Transaction flagged: id={}, reason={}", id, reason);
        return repository.save(tx);
    }

    @Auditable(action = "CREATE", entityType = "FINANCIAL_TRANSACTION", description = "Create financial transaction")
    @Transactional
    public FinancialTransaction createTransaction(FinancialTransaction transaction) {
        FinancialTransaction saved = repository.save(transaction);
        log.info("Transaction created: ref={}, amount={} {}",
            saved.getTransactionRef(), saved.getAmount(), saved.getCurrency());
        return saved;
    }

    @Transactional(readOnly = true)
    public java.util.List<FinancialTransaction> getByCaseId(Integer caseId) {
        return repository.findByRelatedCaseId(caseId);
    }

    @Auditable(action = "UPDATE", entityType = "FINANCIAL_TRANSACTION", description = "Update financial transaction")
    @Transactional
    public FinancialTransaction updateTransaction(UUID id, FinancialTransaction updated) {
        FinancialTransaction tx = getTransaction(id);
        tx.setTransactionRef(updated.getTransactionRef());
        tx.setSenderAccountId(updated.getSenderAccountId());
        tx.setRecipientAccountId(updated.getRecipientAccountId());
        tx.setAmount(updated.getAmount());
        tx.setCurrency(updated.getCurrency());
        tx.setTransactionDate(updated.getTransactionDate());
        tx.setTransactionType(updated.getTransactionType());
        tx.setIsFlagged(updated.getIsFlagged());
        tx.setFlagReason(updated.getFlagReason());
        tx.setRiskScore(updated.getRiskScore());
        tx.setRelatedCaseId(updated.getRelatedCaseId());
        log.info("Transaction updated: id={}", id);
        return repository.save(tx);
    }

    @Auditable(action = "DELETE", entityType = "FINANCIAL_TRANSACTION", description = "Delete financial transaction")
    @Transactional
    public void deleteTransaction(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction", "id", id);
        }
        repository.deleteById(id);
        log.info("Transaction deleted: id={}", id);
    }
}
