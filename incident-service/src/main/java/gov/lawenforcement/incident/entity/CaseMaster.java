package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "case_master")
public class CaseMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_master_id")
    private Integer caseMasterId;

    @Column(name = "crime_no", unique = true, nullable = false, length = 30)
    private String crimeNo;

    @Column(name = "case_no", length = 20)
    private String caseNo;

    @Column(name = "crime_registered_date", nullable = false)
    private LocalDate crimeRegisteredDate;

    @Column(name = "police_person_id")
    private Integer policePersonId;

    @Column(name = "police_station_id")
    private Integer policeStationId;

    @Column(name = "case_category_id")
    private Integer caseCategoryId;

    @Column(name = "gravity_offence_id")
    private Integer gravityOffenceId;

    @Column(name = "crime_major_head_id")
    private Integer crimeMajorHeadId;

    @Column(name = "crime_minor_head_id")
    private Integer crimeMinorHeadId;

    @Column(name = "case_status_id")
    private Integer caseStatusId;

    @Column(name = "court_id")
    private Integer courtId;

    @Column(name = "incident_from_date")
    private Instant incidentFromDate;

    @Column(name = "incident_to_date")
    private Instant incidentToDate;

    @Column(name = "info_received_ps_date")
    private Instant infoReceivedPsDate;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "brief_facts", columnDefinition = "TEXT")
    private String briefFacts;

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public String getCrimeNo() {
        return crimeNo;
    }

    public void setCrimeNo(String crimeNo) {
        this.crimeNo = crimeNo;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public LocalDate getCrimeRegisteredDate() {
        return crimeRegisteredDate;
    }

    public void setCrimeRegisteredDate(LocalDate crimeRegisteredDate) {
        this.crimeRegisteredDate = crimeRegisteredDate;
    }

    public Integer getPolicePersonId() {
        return policePersonId;
    }

    public void setPolicePersonId(Integer policePersonId) {
        this.policePersonId = policePersonId;
    }

    public Integer getPoliceStationId() {
        return policeStationId;
    }

    public void setPoliceStationId(Integer policeStationId) {
        this.policeStationId = policeStationId;
    }

    public Integer getCaseCategoryId() {
        return caseCategoryId;
    }

    public void setCaseCategoryId(Integer caseCategoryId) {
        this.caseCategoryId = caseCategoryId;
    }

    public Integer getGravityOffenceId() {
        return gravityOffenceId;
    }

    public void setGravityOffenceId(Integer gravityOffenceId) {
        this.gravityOffenceId = gravityOffenceId;
    }

    public Integer getCrimeMajorHeadId() {
        return crimeMajorHeadId;
    }

    public void setCrimeMajorHeadId(Integer crimeMajorHeadId) {
        this.crimeMajorHeadId = crimeMajorHeadId;
    }

    public Integer getCrimeMinorHeadId() {
        return crimeMinorHeadId;
    }

    public void setCrimeMinorHeadId(Integer crimeMinorHeadId) {
        this.crimeMinorHeadId = crimeMinorHeadId;
    }

    public Integer getCaseStatusId() {
        return caseStatusId;
    }

    public void setCaseStatusId(Integer caseStatusId) {
        this.caseStatusId = caseStatusId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }

    public Instant getIncidentFromDate() {
        return incidentFromDate;
    }

    public void setIncidentFromDate(Instant incidentFromDate) {
        this.incidentFromDate = incidentFromDate;
    }

    public Instant getIncidentToDate() {
        return incidentToDate;
    }

    public void setIncidentToDate(Instant incidentToDate) {
        this.incidentToDate = incidentToDate;
    }

    public Instant getInfoReceivedPsDate() {
        return infoReceivedPsDate;
    }

    public void setInfoReceivedPsDate(Instant infoReceivedPsDate) {
        this.infoReceivedPsDate = infoReceivedPsDate;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getBriefFacts() {
        return briefFacts;
    }

    public void setBriefFacts(String briefFacts) {
        this.briefFacts = briefFacts;
    }
}
