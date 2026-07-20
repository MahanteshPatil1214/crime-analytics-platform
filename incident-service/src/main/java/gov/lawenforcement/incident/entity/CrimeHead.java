package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crime_head")
public class CrimeHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crime_head_id")
    private Integer crimeHeadId;

    @Column(name = "crime_group_name", nullable = false, length = 200)
    private String crimeGroupName;

    @Column(name = "active", columnDefinition = "BIT")
    private Boolean active;

    public Integer getCrimeHeadId() {
        return crimeHeadId;
    }

    public void setCrimeHeadId(Integer crimeHeadId) {
        this.crimeHeadId = crimeHeadId;
    }

    public String getCrimeGroupName() {
        return crimeGroupName;
    }

    public void setCrimeGroupName(String crimeGroupName) {
        this.crimeGroupName = crimeGroupName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
