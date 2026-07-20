package gov.lawenforcement.financial.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "financial_transactions", indexes = {
    @Index(name = "idx_ft_sender", columnList = "sender_account_id"),
    @Index(name = "idx_ft_recipient", columnList = "recipient_account_id"),
    @Index(name = "idx_ft_date", columnList = "transaction_date"),
    @Index(name = "idx_ft_flagged", columnList = "is_flagged")
})
public class FinancialTransaction {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "transaction_ref", unique = true, nullable = false, length = 64)
    private String transactionRef;

    @Column(name = "sender_account_id", nullable = false, length = 64)
    private String senderAccountId;

    @Column(name = "recipient_account_id", nullable = false, length = 64)
    private String recipientAccountId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "is_flagged", nullable = false)
    private Boolean isFlagged = false;

    @Column(name = "flag_reason", length = 200)
    private String flagReason;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "related_case_id")
    private Integer relatedCaseId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() { this.id = UUID.randomUUID(); this.createdAt = Instant.now(); }

    public enum TransactionType { WIRE, CASH_DEPOSIT, CASH_WITHDRAWAL, CHECK, CRYPTO, TRANSFER }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(String senderAccountId) { this.senderAccountId = senderAccountId; }
    public String getRecipientAccountId() { return recipientAccountId; }
    public void setRecipientAccountId(String recipientAccountId) { this.recipientAccountId = recipientAccountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Instant getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Instant transactionDate) { this.transactionDate = transactionDate; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public Boolean getIsFlagged() { return isFlagged; }
    public void setIsFlagged(Boolean isFlagged) { this.isFlagged = isFlagged; }
    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public Integer getRelatedCaseId() { return relatedCaseId; }
    public void setRelatedCaseId(Integer relatedCaseId) { this.relatedCaseId = relatedCaseId; }
}
