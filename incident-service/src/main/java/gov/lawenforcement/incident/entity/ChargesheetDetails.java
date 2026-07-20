package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chargesheet_details")
public class ChargesheetDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cs_id")
    private Integer csId;

    @Column(name = "case_master_id", nullable = false)
    private Integer caseMasterId;

    @Column(name = "cs_date")
    private Instant csDate;

    @Column(name = "cs_type", length = 1)
    private String csType;

    @Column(name = "police_person_id")
    private Integer policePersonId;

    public Integer getCsId() {
        return csId;
    }

    public void setCsId(Integer csId) {
        this.csId = csId;
    }

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public Instant getCsDate() {
        return csDate;
    }

    public void setCsDate(Instant csDate) {
        this.csDate = csDate;
    }

    public String getCsType() {
        return csType;
    }

    public void setCsType(String csType) {
        this.csType = csType;
    }

    public Integer getPolicePersonId() {
        return policePersonId;
    }

    public void setPolicePersonId(Integer policePersonId) {
        this.policePersonId = policePersonId;
    }
}
