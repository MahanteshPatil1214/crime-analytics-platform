package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_type")
public class UnitType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_type_id")
    private Integer unitTypeId;

    @Column(name = "unit_type_name", nullable = false, length = 100)
    private String unitTypeName;

    @Column(name = "city_dist_state", length = 20)
    private String cityDistState;

    @Column(name = "hierarchy")
    private Integer hierarchy;

    @Column(name = "active", columnDefinition = "BIT")
    private Boolean active;

    public Integer getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Integer unitTypeId) {
        this.unitTypeId = unitTypeId;
    }

    public String getUnitTypeName() {
        return unitTypeName;
    }

    public void setUnitTypeName(String unitTypeName) {
        this.unitTypeName = unitTypeName;
    }

    public String getCityDistState() {
        return cityDistState;
    }

    public void setCityDistState(String cityDistState) {
        this.cityDistState = cityDistState;
    }

    public Integer getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Integer hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
