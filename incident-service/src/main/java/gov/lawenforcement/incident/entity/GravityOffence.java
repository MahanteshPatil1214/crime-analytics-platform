package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gravity_offence")
public class GravityOffence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gravity_offence_id")
    private Integer gravityOffenceId;

    @Column(name = "lookup_value", nullable = false, length = 100)
    private String lookupValue;

    public Integer getGravityOffenceId() {
        return gravityOffenceId;
    }

    public void setGravityOffenceId(Integer gravityOffenceId) {
        this.gravityOffenceId = gravityOffenceId;
    }

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }
}
