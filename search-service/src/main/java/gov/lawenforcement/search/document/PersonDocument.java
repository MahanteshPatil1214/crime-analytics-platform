package gov.lawenforcement.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "persons")
public class PersonDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Integer)
    private Integer dbId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Keyword)
    private String gender;

    @Field(type = FieldType.Keyword)
    private String personType;

    @Field(type = FieldType.Keyword)
    private List<String> caseCrimeNos;

    @Field(type = FieldType.Integer)
    private List<Integer> caseIds;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getDbId() { return dbId; }
    public void setDbId(Integer dbId) { this.dbId = dbId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public List<String> getCaseCrimeNos() { return caseCrimeNos; }
    public void setCaseCrimeNos(List<String> caseCrimeNos) { this.caseCrimeNos = caseCrimeNos; }
    public List<Integer> getCaseIds() { return caseIds; }
    public void setCaseIds(List<Integer> caseIds) { this.caseIds = caseIds; }
}
