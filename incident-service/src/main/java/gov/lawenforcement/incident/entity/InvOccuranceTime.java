package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "inv_occurance_time")
public class InvOccuranceTime {

    @Id
    @Column(name = "case_master_id")
    private Integer caseMasterId;

    @Column(name = "occurrence_from")
    private Instant occurrenceFrom;

    @Column(name = "occurrence_to")
    private Instant occurrenceTo;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public Instant getOccurrenceFrom() {
        return occurrenceFrom;
    }

    public void setOccurrenceFrom(Instant occurrenceFrom) {
        this.occurrenceFrom = occurrenceFrom;
    }

    public Instant getOccurrenceTo() {
        return occurrenceTo;
    }

    public void setOccurrenceTo(Instant occurrenceTo) {
        this.occurrenceTo = occurrenceTo;
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
}
