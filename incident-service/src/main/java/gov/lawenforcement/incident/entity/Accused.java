package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "accused")
public class Accused {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accused_master_id")
    private Integer accusedMasterId;

    @Column(name = "case_master_id", nullable = false)
    private Integer caseMasterId;

    @Column(name = "accused_name", nullable = false, length = 200)
    private String accusedName;

    @Column(name = "age_year")
    private Integer ageYear;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "person_id", length = 10)
    private String personId;

    public Integer getAccusedMasterId() {
        return accusedMasterId;
    }

    public void setAccusedMasterId(Integer accusedMasterId) {
        this.accusedMasterId = accusedMasterId;
    }

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public String getAccusedName() {
        return accusedName;
    }

    public void setAccusedName(String accusedName) {
        this.accusedName = accusedName;
    }

    public Integer getAgeYear() {
        return ageYear;
    }

    public void setAgeYear(Integer ageYear) {
        this.ageYear = ageYear;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
