package gov.lawenforcement.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "financial_transactions")
public class FinancialDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String transactionRef;

    @Field(type = FieldType.Keyword)
    private String senderAccountId;

    @Field(type = FieldType.Keyword)
    private String recipientAccountId;

    @Field(type = FieldType.Double)
    private Double amount;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Keyword)
    private String transactionDate;

    @Field(type = FieldType.Keyword)
    private String transactionType;

    @Field(type = FieldType.Boolean)
    private Boolean flagged;

    @Field(type = FieldType.Keyword)
    private String relatedCaseCrimeNo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(String senderAccountId) { this.senderAccountId = senderAccountId; }
    public String getRecipientAccountId() { return recipientAccountId; }
    public void setRecipientAccountId(String recipientAccountId) { this.recipientAccountId = recipientAccountId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public Boolean getFlagged() { return flagged; }
    public void setFlagged(Boolean flagged) { this.flagged = flagged; }
    public String getRelatedCaseCrimeNo() { return relatedCaseCrimeNo; }
    public void setRelatedCaseCrimeNo(String relatedCaseCrimeNo) { this.relatedCaseCrimeNo = relatedCaseCrimeNo; }
}
