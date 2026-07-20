package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "arrest_surrender")
public class ArrestSurrender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "arrest_surrender_id")
    private Integer arrestSurrenderId;

    @Column(name = "case_master_id", nullable = false)
    private Integer caseMasterId;

    @Column(name = "arrest_surrender_type_id")
    private Integer arrestSurrenderTypeId;

    @Column(name = "arrest_surrender_date")
    private LocalDate arrestSurrenderDate;

    @Column(name = "arrest_surrender_state_id")
    private Integer arrestSurrenderStateId;

    @Column(name = "arrest_surrender_district_id")
    private Integer arrestSurrenderDistrictId;

    @Column(name = "police_station_id")
    private Integer policeStationId;

    @Column(name = "io_id")
    private Integer ioId;

    @Column(name = "court_id")
    private Integer courtId;

    public Integer getArrestSurrenderId() {
        return arrestSurrenderId;
    }

    public void setArrestSurrenderId(Integer arrestSurrenderId) {
        this.arrestSurrenderId = arrestSurrenderId;
    }

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public Integer getArrestSurrenderTypeId() {
        return arrestSurrenderTypeId;
    }

    public void setArrestSurrenderTypeId(Integer arrestSurrenderTypeId) {
        this.arrestSurrenderTypeId = arrestSurrenderTypeId;
    }

    public LocalDate getArrestSurrenderDate() {
        return arrestSurrenderDate;
    }

    public void setArrestSurrenderDate(LocalDate arrestSurrenderDate) {
        this.arrestSurrenderDate = arrestSurrenderDate;
    }

    public Integer getArrestSurrenderStateId() {
        return arrestSurrenderStateId;
    }

    public void setArrestSurrenderStateId(Integer arrestSurrenderStateId) {
        this.arrestSurrenderStateId = arrestSurrenderStateId;
    }

    public Integer getArrestSurrenderDistrictId() {
        return arrestSurrenderDistrictId;
    }

    public void setArrestSurrenderDistrictId(Integer arrestSurrenderDistrictId) {
        this.arrestSurrenderDistrictId = arrestSurrenderDistrictId;
    }

    public Integer getPoliceStationId() {
        return policeStationId;
    }

    public void setPoliceStationId(Integer policeStationId) {
        this.policeStationId = policeStationId;
    }

    public Integer getIoId() {
        return ioId;
    }

    public void setIoId(Integer ioId) {
        this.ioId = ioId;
    }

    public Integer getCourtId() {
        return courtId;
    }

    public void setCourtId(Integer courtId) {
        this.courtId = courtId;
    }
}
