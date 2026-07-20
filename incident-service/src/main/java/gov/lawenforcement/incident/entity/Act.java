package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "act")
public class Act {

    @Id
    @Column(name = "act_code", length = 20)
    private String actCode;

    @Column(name = "act_description", nullable = false, length = 500)
    private String actDescription;

    @Column(name = "short_name", length = 50)
    private String shortName;

    @Column(name = "active", columnDefinition = "BIT")
    private Boolean active;

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getActDescription() {
        return actDescription;
    }

    public void setActDescription(String actDescription) {
        this.actDescription = actDescription;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
