package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "victim")
public class Victim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "victim_master_id")
    private Integer victimMasterId;

    @Column(name = "case_master_id", nullable = false)
    private Integer caseMasterId;

    @Column(name = "victim_name", nullable = false, length = 200)
    private String victimName;

    @Column(name = "age_year")
    private Integer ageYear;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "victim_police", length = 5, columnDefinition = "VARCHAR(5) DEFAULT '0'")
    private String victimPolice;

    public Integer getVictimMasterId() {
        return victimMasterId;
    }

    public void setVictimMasterId(Integer victimMasterId) {
        this.victimMasterId = victimMasterId;
    }

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
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

    public String getVictimPolice() {
        return victimPolice;
    }

    public void setVictimPolice(String victimPolice) {
        this.victimPolice = victimPolice;
    }
}
