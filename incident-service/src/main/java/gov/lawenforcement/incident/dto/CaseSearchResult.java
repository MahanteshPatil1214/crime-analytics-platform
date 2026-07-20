package gov.lawenforcement.incident.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CaseSearchResult {
    private Integer caseMasterId;
    private String crimeNo;
    private String caseNo;
    private LocalDate crimeRegisteredDate;
    private String briefFacts;
    private Integer caseStatusId;
    private String statusName;
    private Integer crimeMajorHeadId;
    private String crimeHeadName;
    private Integer policeStationId;
    private String policeStationName;
    private Integer districtId;
    private String districtName;
    private List<String> accusedNames;
    private List<String> victimNames;
    private List<String> complainantNames;
    private List<String> actSections;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Integer getCaseMasterId() { return caseMasterId; }
    public void setCaseMasterId(Integer caseMasterId) { this.caseMasterId = caseMasterId; }
    public String getCrimeNo() { return crimeNo; }
    public void setCrimeNo(String crimeNo) { this.crimeNo = crimeNo; }
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public LocalDate getCrimeRegisteredDate() { return crimeRegisteredDate; }
    public void setCrimeRegisteredDate(LocalDate crimeRegisteredDate) { this.crimeRegisteredDate = crimeRegisteredDate; }
    public String getBriefFacts() { return briefFacts; }
    public void setBriefFacts(String briefFacts) { this.briefFacts = briefFacts; }
    public Integer getCaseStatusId() { return caseStatusId; }
    public void setCaseStatusId(Integer caseStatusId) { this.caseStatusId = caseStatusId; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Integer getCrimeMajorHeadId() { return crimeMajorHeadId; }
    public void setCrimeMajorHeadId(Integer crimeMajorHeadId) { this.crimeMajorHeadId = crimeMajorHeadId; }
    public String getCrimeHeadName() { return crimeHeadName; }
    public void setCrimeHeadName(String crimeHeadName) { this.crimeHeadName = crimeHeadName; }
    public Integer getPoliceStationId() { return policeStationId; }
    public void setPoliceStationId(Integer policeStationId) { this.policeStationId = policeStationId; }
    public String getPoliceStationName() { return policeStationName; }
    public void setPoliceStationName(String policeStationName) { this.policeStationName = policeStationName; }
    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public List<String> getAccusedNames() { return accusedNames; }
    public void setAccusedNames(List<String> accusedNames) { this.accusedNames = accusedNames; }
    public List<String> getVictimNames() { return victimNames; }
    public void setVictimNames(List<String> victimNames) { this.victimNames = victimNames; }
    public List<String> getComplainantNames() { return complainantNames; }
    public void setComplainantNames(List<String> complainantNames) { this.complainantNames = complainantNames; }
    public List<String> getActSections() { return actSections; }
    public void setActSections(List<String> actSections) { this.actSections = actSections; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
}
