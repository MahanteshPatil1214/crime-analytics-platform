package gov.lawenforcement.analytics.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "socio_demographics")
public class SocioDemographic {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "census_tract", unique = true, nullable = false, length = 20)
    private String censusTract;

    @Column(name = "region_name", length = 200)
    private String regionName;

    @Column(name = "boundary")
    private String boundary;

    @Column(name = "population")
    private Integer population;

    @Column(name = "population_density")
    private Double populationDensity;

    @Column(name = "median_income")
    private Double medianIncome;

    @Column(name = "unemployment_rate")
    private Double unemploymentRate;

    @Column(name = "poverty_rate")
    private Double povertyRate;

    @Column(name = "education_below_highschool_rate")
    private Double educationBelowHighschoolRate;

    @Column(name = "housing_vacancy_rate")
    private Double housingVacancyRate;

    @Column(name = "single_parent_household_rate")
    private Double singleParentHouseholdRate;

    @Column(name = "gini_index")
    private Double giniIndex;

    @Column(name = "data_year")
    private Integer dataYear;

    @PrePersist
    protected void onCreate() { this.id = UUID.randomUUID(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCensusTract() { return censusTract; }
    public void setCensusTract(String censusTract) { this.censusTract = censusTract; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getBoundary() { return boundary; }
    public void setBoundary(String boundary) { this.boundary = boundary; }
    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
    public Double getPopulationDensity() { return populationDensity; }
    public void setPopulationDensity(Double populationDensity) { this.populationDensity = populationDensity; }
    public Double getMedianIncome() { return medianIncome; }
    public void setMedianIncome(Double medianIncome) { this.medianIncome = medianIncome; }
    public Double getUnemploymentRate() { return unemploymentRate; }
    public void setUnemploymentRate(Double unemploymentRate) { this.unemploymentRate = unemploymentRate; }
    public Double getPovertyRate() { return povertyRate; }
    public void setPovertyRate(Double povertyRate) { this.povertyRate = povertyRate; }
    public Double getEducationBelowHighschoolRate() { return educationBelowHighschoolRate; }
    public void setEducationBelowHighschoolRate(Double r) { this.educationBelowHighschoolRate = r; }
    public Double getHousingVacancyRate() { return housingVacancyRate; }
    public void setHousingVacancyRate(Double housingVacancyRate) { this.housingVacancyRate = housingVacancyRate; }
    public Double getSingleParentHouseholdRate() { return singleParentHouseholdRate; }
    public void setSingleParentHouseholdRate(Double singleParentHouseholdRate) { this.singleParentHouseholdRate = singleParentHouseholdRate; }
    public Double getGiniIndex() { return giniIndex; }
    public void setGiniIndex(Double giniIndex) { this.giniIndex = giniIndex; }
    public Integer getDataYear() { return dataYear; }
    public void setDataYear(Integer dataYear) { this.dataYear = dataYear; }
}
