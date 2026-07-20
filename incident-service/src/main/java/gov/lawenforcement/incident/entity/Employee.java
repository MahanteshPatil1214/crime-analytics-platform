package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "rank_id")
    private Integer rankId;

    @Column(name = "designation_id")
    private Integer designationId;

    @Column(name = "kgid", unique = true, length = 30)
    private String kgid;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "employee_dob")
    private LocalDate employeeDob;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "blood_group_id")
    private Integer bloodGroupId;

    @Column(name = "physically_challenged", columnDefinition = "BIT")
    private Boolean physicallyChallenged;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public Integer getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Integer designationId) {
        this.designationId = designationId;
    }

    public String getKgid() {
        return kgid;
    }

    public void setKgid(String kgid) {
        this.kgid = kgid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getEmployeeDob() {
        return employeeDob;
    }

    public void setEmployeeDob(LocalDate employeeDob) {
        this.employeeDob = employeeDob;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public Integer getBloodGroupId() {
        return bloodGroupId;
    }

    public void setBloodGroupId(Integer bloodGroupId) {
        this.bloodGroupId = bloodGroupId;
    }

    public Boolean getPhysicallyChallenged() {
        return physicallyChallenged;
    }

    public void setPhysicallyChallenged(Boolean physicallyChallenged) {
        this.physicallyChallenged = physicallyChallenged;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
