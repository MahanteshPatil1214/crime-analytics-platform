package gov.lawenforcement.financial.repository;

import gov.lawenforcement.financial.entity.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID> {

    List<FinancialTransaction> findBySenderAccountIdOrRecipientAccountId(
        String senderAccountId, String recipientAccountId);

    Page<FinancialTransaction> findByIsFlaggedTrue(Pageable pageable);

    List<FinancialTransaction> findByTransactionType(FinancialTransaction.TransactionType transactionType);

    List<FinancialTransaction> findByRiskScoreBetween(Double minRisk, Double maxRisk);

    long countByIsFlagged(Boolean isFlagged);

    List<FinancialTransaction> findByRelatedCaseId(Integer relatedCaseId);

    @Query(value = """
        SELECT t FROM FinancialTransaction t
        WHERE (:accountId IS NULL OR t.senderAccountId = :accountId OR t.recipientAccountId = :accountId)
          AND (:type IS NULL OR t.transactionType = :type)
          AND (:flagged IS NULL OR t.isFlagged = :flagged)
          AND (:minRisk IS NULL OR t.riskScore >= :minRisk)
          AND (:maxRisk IS NULL OR t.riskScore <= :maxRisk)
        ORDER BY t.transactionDate DESC
        """, countQuery = """
        SELECT count(t) FROM FinancialTransaction t
        WHERE (:accountId IS NULL OR t.senderAccountId = :accountId OR t.recipientAccountId = :accountId)
          AND (:type IS NULL OR t.transactionType = :type)
          AND (:flagged IS NULL OR t.isFlagged = :flagged)
          AND (:minRisk IS NULL OR t.riskScore >= :minRisk)
          AND (:maxRisk IS NULL OR t.riskScore <= :maxRisk)
        """)
    Page<FinancialTransaction> search(
        @Param("accountId") String accountId,
        @Param("type") FinancialTransaction.TransactionType type,
        @Param("flagged") Boolean flagged,
        @Param("minRisk") Double minRisk,
        @Param("maxRisk") Double maxRisk,
        Pageable pageable);
}
