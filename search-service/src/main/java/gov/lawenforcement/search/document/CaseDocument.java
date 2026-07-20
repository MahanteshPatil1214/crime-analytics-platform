package gov.lawenforcement.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "cases")
public class CaseDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String crimeNo;

    @Field(type = FieldType.Integer)
    private Integer caseMasterId;

    @Field(type = FieldType.Keyword)
    private String caseNo;

    @Field(type = FieldType.Keyword)
    private String crimeRegisteredDate;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String briefFacts;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

    @Field(type = FieldType.Keyword)
    private String districtName;

    @Field(type = FieldType.Keyword)
    private String crimeHeadName;

    @Field(type = FieldType.Keyword)
    private String statusName;

    @Field(type = FieldType.Keyword)
    private String policeStationName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private List<String> accusedNames;

    @Field(type = FieldType.Text, analyzer = "standard")
    private List<String> victimNames;

    @Field(type = FieldType.Text, analyzer = "standard")
    private List<String> complainantNames;

    @Field(type = FieldType.Keyword)
    private List<String> actSections;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;

    public String getCrimeNo() { return crimeNo; }
    public void setCrimeNo(String crimeNo) { this.crimeNo = crimeNo; }
    public Integer getCaseMasterId() { return caseMasterId; }
    public void setCaseMasterId(Integer caseMasterId) { this.caseMasterId = caseMasterId; }
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public String getCrimeRegisteredDate() { return crimeRegisteredDate; }
    public void setCrimeRegisteredDate(String crimeRegisteredDate) { this.crimeRegisteredDate = crimeRegisteredDate; }
    public String getBriefFacts() { return briefFacts; }
    public void setBriefFacts(String briefFacts) { this.briefFacts = briefFacts; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public String getCrimeHeadName() { return crimeHeadName; }
    public void setCrimeHeadName(String crimeHeadName) { this.crimeHeadName = crimeHeadName; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public String getPoliceStationName() { return policeStationName; }
    public void setPoliceStationName(String policeStationName) { this.policeStationName = policeStationName; }
    public List<String> getAccusedNames() { return accusedNames; }
    public void setAccusedNames(List<String> accusedNames) { this.accusedNames = accusedNames; }
    public List<String> getVictimNames() { return victimNames; }
    public void setVictimNames(List<String> victimNames) { this.victimNames = victimNames; }
    public List<String> getComplainantNames() { return complainantNames; }
    public void setComplainantNames(List<String> complainantNames) { this.complainantNames = complainantNames; }
    public List<String> getActSections() { return actSections; }
    public void setActSections(List<String> actSections) { this.actSections = actSections; }
    public String getSearchableText() { return searchableText; }
    public void setSearchableText(String searchableText) { this.searchableText = searchableText; }
}
