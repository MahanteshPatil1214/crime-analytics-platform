package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "inv_arrest_surrender_accused")
@IdClass(InvArrestSurrenderAccused.InvArrestSurrenderAccusedId.class)
public class InvArrestSurrenderAccused {

    @Id
    @Column(name = "arrest_surrender_id")
    private Integer arrestSurrenderId;

    @Id
    @Column(name = "accused_master_id")
    private Integer accusedMasterId;

    @Column(name = "is_accused", columnDefinition = "BIT")
    private Boolean isAccused;

    @Column(name = "is_complainant_accused", columnDefinition = "BIT")
    private Boolean isComplainantAccused;

    public Integer getArrestSurrenderId() {
        return arrestSurrenderId;
    }

    public void setArrestSurrenderId(Integer arrestSurrenderId) {
        this.arrestSurrenderId = arrestSurrenderId;
    }

    public Integer getAccusedMasterId() {
        return accusedMasterId;
    }

    public void setAccusedMasterId(Integer accusedMasterId) {
        this.accusedMasterId = accusedMasterId;
    }

    public Boolean getIsAccused() {
        return isAccused;
    }

    public void setIsAccused(Boolean isAccused) {
        this.isAccused = isAccused;
    }

    public Boolean getIsComplainantAccused() {
        return isComplainantAccused;
    }

    public void setIsComplainantAccused(Boolean isComplainantAccused) {
        this.isComplainantAccused = isComplainantAccused;
    }

    public static class InvArrestSurrenderAccusedId implements Serializable {

        private Integer arrestSurrenderId;
        private Integer accusedMasterId;

        public InvArrestSurrenderAccusedId() {
        }

        public InvArrestSurrenderAccusedId(Integer arrestSurrenderId, Integer accusedMasterId) {
            this.arrestSurrenderId = arrestSurrenderId;
            this.accusedMasterId = accusedMasterId;
        }

        public Integer getArrestSurrenderId() {
            return arrestSurrenderId;
        }

        public void setArrestSurrenderId(Integer arrestSurrenderId) {
            this.arrestSurrenderId = arrestSurrenderId;
        }

        public Integer getAccusedMasterId() {
            return accusedMasterId;
        }

        public void setAccusedMasterId(Integer accusedMasterId) {
            this.accusedMasterId = accusedMasterId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InvArrestSurrenderAccusedId that = (InvArrestSurrenderAccusedId) o;
            return Objects.equals(arrestSurrenderId, that.arrestSurrenderId)
                    && Objects.equals(accusedMasterId, that.accusedMasterId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(arrestSurrenderId, accusedMasterId);
        }
    }
}
