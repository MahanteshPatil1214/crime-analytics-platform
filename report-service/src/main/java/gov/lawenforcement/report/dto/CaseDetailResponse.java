package gov.lawenforcement.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CaseDetailResponse {

    @JsonProperty("case")
    private CaseInfo caseInfo;
    private List<ComplainantInfo> complainants;
    private List<VictimInfo> victims;
    private List<AccusedInfo> accused;
    private List<ArrestInfo> arrests;
    private List<ActSectionInfo> actSections;
    private List<ChargesheetInfo> chargesheets;
    private OccurrenceTimeInfo occurrenceTime;

    public CaseInfo getCaseInfo() { return caseInfo; }
    public void setCaseInfo(CaseInfo caseInfo) { this.caseInfo = caseInfo; }
    public List<ComplainantInfo> getComplainants() { return complainants; }
    public void setComplainants(List<ComplainantInfo> complainants) { this.complainants = complainants; }
    public List<VictimInfo> getVictims() { return victims; }
    public void setVictims(List<VictimInfo> victims) { this.victims = victims; }
    public List<AccusedInfo> getAccused() { return accused; }
    public void setAccused(List<AccusedInfo> accused) { this.accused = accused; }
    public List<ArrestInfo> getArrests() { return arrests; }
    public void setArrests(List<ArrestInfo> arrests) { this.arrests = arrests; }
    public List<ActSectionInfo> getActSections() { return actSections; }
    public void setActSections(List<ActSectionInfo> actSections) { this.actSections = actSections; }
    public List<ChargesheetInfo> getChargesheets() { return chargesheets; }
    public void setChargesheets(List<ChargesheetInfo> chargesheets) { this.chargesheets = chargesheets; }
    public OccurrenceTimeInfo getOccurrenceTime() { return occurrenceTime; }
    public void setOccurrenceTime(OccurrenceTimeInfo occurrenceTime) { this.occurrenceTime = occurrenceTime; }

    public static class CaseInfo {
        private Integer caseMasterId;
        private String crimeNo;
        private String caseNo;
        private String crimeRegisteredDate;
        private Integer policePersonId;
        private Integer policeStationId;
        private Integer caseCategoryId;
        private Integer gravityOffenceId;
        private Integer crimeMajorHeadId;
        private Integer crimeMinorHeadId;
        private Integer caseStatusId;
        private Integer courtId;
        private String incidentFromDate;
        private String incidentToDate;
        private String infoReceivedPsDate;
        private Double latitude;
        private Double longitude;
        private String briefFacts;

        public Integer getCaseMasterId() { return caseMasterId; }
        public void setCaseMasterId(Integer caseMasterId) { this.caseMasterId = caseMasterId; }
        public String getCrimeNo() { return crimeNo; }
        public void setCrimeNo(String crimeNo) { this.crimeNo = crimeNo; }
        public String getCaseNo() { return caseNo; }
        public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
        public String getCrimeRegisteredDate() { return crimeRegisteredDate; }
        public void setCrimeRegisteredDate(String crimeRegisteredDate) { this.crimeRegisteredDate = crimeRegisteredDate; }
        public Integer getPolicePersonId() { return policePersonId; }
        public void setPolicePersonId(Integer policePersonId) { this.policePersonId = policePersonId; }
        public Integer getPoliceStationId() { return policeStationId; }
        public void setPoliceStationId(Integer policeStationId) { this.policeStationId = policeStationId; }
        public Integer getCaseCategoryId() { return caseCategoryId; }
        public void setCaseCategoryId(Integer caseCategoryId) { this.caseCategoryId = caseCategoryId; }
        public Integer getGravityOffenceId() { return gravityOffenceId; }
        public void setGravityOffenceId(Integer gravityOffenceId) { this.gravityOffenceId = gravityOffenceId; }
        public Integer getCrimeMajorHeadId() { return crimeMajorHeadId; }
        public void setCrimeMajorHeadId(Integer crimeMajorHeadId) { this.crimeMajorHeadId = crimeMajorHeadId; }
        public Integer getCrimeMinorHeadId() { return crimeMinorHeadId; }
        public void setCrimeMinorHeadId(Integer crimeMinorHeadId) { this.crimeMinorHeadId = crimeMinorHeadId; }
        public Integer getCaseStatusId() { return caseStatusId; }
        public void setCaseStatusId(Integer caseStatusId) { this.caseStatusId = caseStatusId; }
        public Integer getCourtId() { return courtId; }
        public void setCourtId(Integer courtId) { this.courtId = courtId; }
        public String getIncidentFromDate() { return incidentFromDate; }
        public void setIncidentFromDate(String incidentFromDate) { this.incidentFromDate = incidentFromDate; }
        public String getIncidentToDate() { return incidentToDate; }
        public void setIncidentToDate(String incidentToDate) { this.incidentToDate = incidentToDate; }
        public String getInfoReceivedPsDate() { return infoReceivedPsDate; }
        public void setInfoReceivedPsDate(String infoReceivedPsDate) { this.infoReceivedPsDate = infoReceivedPsDate; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getBriefFacts() { return briefFacts; }
        public void setBriefFacts(String briefFacts) { this.briefFacts = briefFacts; }
    }

    public static class ComplainantInfo {
        private Integer complainantId;
        private String complainantName;
        private Integer ageYear;
        private Integer genderId;

        public Integer getComplainantId() { return complainantId; }
        public void setComplainantId(Integer complainantId) { this.complainantId = complainantId; }
        public String getComplainantName() { return complainantName; }
        public void setComplainantName(String complainantName) { this.complainantName = complainantName; }
        public Integer getAgeYear() { return ageYear; }
        public void setAgeYear(Integer ageYear) { this.ageYear = ageYear; }
        public Integer getGenderId() { return genderId; }
        public void setGenderId(Integer genderId) { this.genderId = genderId; }
    }

    public static class VictimInfo {
        private Integer victimMasterId;
        private String victimName;
        private Integer ageYear;
        private Integer genderId;

        public Integer getVictimMasterId() { return victimMasterId; }
        public void setVictimMasterId(Integer victimMasterId) { this.victimMasterId = victimMasterId; }
        public String getVictimName() { return victimName; }
        public void setVictimName(String victimName) { this.victimName = victimName; }
        public Integer getAgeYear() { return ageYear; }
        public void setAgeYear(Integer ageYear) { this.ageYear = ageYear; }
        public Integer getGenderId() { return genderId; }
        public void setGenderId(Integer genderId) { this.genderId = genderId; }
    }

    public static class AccusedInfo {
        private Integer accusedMasterId;
        private String accusedName;
        private Integer ageYear;
        private Integer genderId;
        private String personId;

        public Integer getAccusedMasterId() { return accusedMasterId; }
        public void setAccusedMasterId(Integer accusedMasterId) { this.accusedMasterId = accusedMasterId; }
        public String getAccusedName() { return accusedName; }
        public void setAccusedName(String accusedName) { this.accusedName = accusedName; }
        public Integer getAgeYear() { return ageYear; }
        public void setAgeYear(Integer ageYear) { this.ageYear = ageYear; }
        public Integer getGenderId() { return genderId; }
        public void setGenderId(Integer genderId) { this.genderId = genderId; }
        public String getPersonId() { return personId; }
        public void setPersonId(String personId) { this.personId = personId; }
    }

    public static class ArrestInfo {
        private Integer arrestSurrenderId;
        private Integer arrestSurrenderTypeId;
        private String arrestSurrenderDate;
        private Integer ioId;
        private Integer courtId;

        public Integer getArrestSurrenderId() { return arrestSurrenderId; }
        public void setArrestSurrenderId(Integer arrestSurrenderId) { this.arrestSurrenderId = arrestSurrenderId; }
        public Integer getArrestSurrenderTypeId() { return arrestSurrenderTypeId; }
        public void setArrestSurrenderTypeId(Integer arrestSurrenderTypeId) { this.arrestSurrenderTypeId = arrestSurrenderTypeId; }
        public String getArrestSurrenderDate() { return arrestSurrenderDate; }
        public void setArrestSurrenderDate(String arrestSurrenderDate) { this.arrestSurrenderDate = arrestSurrenderDate; }
        public Integer getIoId() { return ioId; }
        public void setIoId(Integer ioId) { this.ioId = ioId; }
        public Integer getCourtId() { return courtId; }
        public void setCourtId(Integer courtId) { this.courtId = courtId; }
    }

    public static class ActSectionInfo {
        private Integer caseMasterId;
        private String actCode;
        private String sectionCode;

        public Integer getCaseMasterId() { return caseMasterId; }
        public void setCaseMasterId(Integer caseMasterId) { this.caseMasterId = caseMasterId; }
        public String getActCode() { return actCode; }
        public void setActCode(String actCode) { this.actCode = actCode; }
        public String getSectionCode() { return sectionCode; }
        public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }
    }

    public static class ChargesheetInfo {
        private Integer csId;
        private String csDate;
        private String csType;

        public Integer getCsId() { return csId; }
        public void setCsId(Integer csId) { this.csId = csId; }
        public String getCsDate() { return csDate; }
        public void setCsDate(String csDate) { this.csDate = csDate; }
        public String getCsType() { return csType; }
        public void setCsType(String csType) { this.csType = csType; }
    }

    public static class OccurrenceTimeInfo {
        private String occurrenceFrom;
        private String occurrenceTo;

        public String getOccurrenceFrom() { return occurrenceFrom; }
        public void setOccurrenceFrom(String occurrenceFrom) { this.occurrenceFrom = occurrenceFrom; }
        public String getOccurrenceTo() { return occurrenceTo; }
        public void setOccurrenceTo(String occurrenceTo) { this.occurrenceTo = occurrenceTo; }
    }
}
