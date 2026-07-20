package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "case_category")
public class CaseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_category_id")
    private Integer caseCategoryId;

    @Column(name = "lookup_value", nullable = false, length = 50)
    private String lookupValue;

    public Integer getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Integer caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }
}
