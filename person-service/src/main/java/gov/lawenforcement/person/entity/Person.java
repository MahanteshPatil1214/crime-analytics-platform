package gov.lawenforcement.person.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "persons", indexes = {
    @Index(name = "idx_person_biometric", columnList = "biometric_id"),
    @Index(name = "idx_person_dob", columnList = "date_of_birth")
})
public class Person {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "biometric_id", unique = true, length = 64)
    private String biometricId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "address_hash", length = 64)
    private String addressHash;

    @Column(name = "phone_hash", length = 64)
    private String phoneHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false)
    private PersonType personType;

    @Column(name = "conviction_count", nullable = false)
    private Integer convictionCount = 0;

    @Column(name = "is_known_offender", nullable = false)
    private Boolean isKnownOffender = false;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "risk_score_updated_at")
    private Instant riskScoreUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() { this.id = UUID.randomUUID(); this.createdAt = Instant.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = Instant.now(); }

    public enum PersonType { SUSPECT, VICTIM, WITNESS, ARRESTED, CONVICTED }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getBiometricId() { return biometricId; }
    public void setBiometricId(String biometricId) { this.biometricId = biometricId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public PersonType getPersonType() { return personType; }
    public void setPersonType(PersonType personType) { this.personType = personType; }
    public Integer getConvictionCount() { return convictionCount; }
    public void setConvictionCount(Integer convictionCount) { this.convictionCount = convictionCount; }
    public Boolean getIsKnownOffender() { return isKnownOffender; }
    public void setIsKnownOffender(Boolean isKnownOffender) { this.isKnownOffender = isKnownOffender; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
}
