package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "case_status_master")
public class CaseStatusMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_status_id")
    private Integer caseStatusId;

    @Column(name = "case_status_name", nullable = false, length = 100)
    private String caseStatusName;

    public Integer getCaseStatusId() {
        return caseStatusId;
    }

    public void setCaseStatusId(Integer caseStatusId) {
        this.caseStatusId = caseStatusId;
    }

    public String getCaseStatusName() {
        return caseStatusName;
    }

    public void setCaseStatusName(String caseStatusName) {
        this.caseStatusName = caseStatusName;
    }
}
