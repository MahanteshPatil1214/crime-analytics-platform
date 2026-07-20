package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "complainant_details")
public class ComplainantDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complainant_id")
    private Integer complainantId;

    @Column(name = "case_master_id", nullable = false)
    private Integer caseMasterId;

    @Column(name = "complainant_name", nullable = false, length = 200)
    private String complainantName;

    @Column(name = "age_year")
    private Integer ageYear;

    @Column(name = "occupation_id")
    private Integer occupationId;

    @Column(name = "religion_id")
    private Integer religionId;

    @Column(name = "caste_id")
    private Integer casteId;

    @Column(name = "gender_id")
    private Integer genderId;

    public Integer getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(Integer complainantId) {
        this.complainantId = complainantId;
    }

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public String getComplainantName() {
        return complainantName;
    }

    public void setComplainantName(String complainantName) {
        this.complainantName = complainantName;
    }

    public Integer getAgeYear() {
        return ageYear;
    }

    public void setAgeYear(Integer ageYear) {
        this.ageYear = ageYear;
    }

    public Integer getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Integer occupationId) {
        this.occupationId = occupationId;
    }

    public Integer getReligionId() {
        return religionId;
    }

    public void setReligionId(Integer religionId) {
        this.religionId = religionId;
    }

    public Integer getCasteId() {
        return casteId;
    }

    public void setCasteId(Integer casteId) {
        this.casteId = casteId;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }
}
