package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "act_section_association")
@IdClass(ActSectionAssociation.ActSectionAssociationId.class)
public class ActSectionAssociation {

    @Id
    @Column(name = "case_master_id")
    private Integer caseMasterId;

    @Id
    @Column(name = "act_code", length = 20)
    private String actCode;

    @Id
    @Column(name = "section_code", length = 20)
    private String sectionCode;

    @Column(name = "act_order_id")
    private Integer actOrderId;

    @Column(name = "section_order_id")
    private Integer sectionOrderId;

    public Integer getCaseMasterId() {
        return caseMasterId;
    }

    public void setCaseMasterId(Integer caseMasterId) {
        this.caseMasterId = caseMasterId;
    }

    public String getActCode() {
        return actCode;
    }

    public void setActCode(String actCode) {
        this.actCode = actCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public Integer getActOrderId() {
        return actOrderId;
    }

    public void setActOrderId(Integer actOrderId) {
        this.actOrderId = actOrderId;
    }

    public Integer getSectionOrderId() {
        return sectionOrderId;
    }

    public void setSectionOrderId(Integer sectionOrderId) {
        this.sectionOrderId = sectionOrderId;
    }

    public static class ActSectionAssociationId implements Serializable {

        private Integer caseMasterId;
        private String actCode;
        private String sectionCode;

        public ActSectionAssociationId() {
        }

        public ActSectionAssociationId(Integer caseMasterId, String actCode, String sectionCode) {
            this.caseMasterId = caseMasterId;
            this.actCode = actCode;
            this.sectionCode = sectionCode;
        }

        public Integer getCaseMasterId() {
            return caseMasterId;
        }

        public void setCaseMasterId(Integer caseMasterId) {
            this.caseMasterId = caseMasterId;
        }

        public String getActCode() {
            return actCode;
        }

        public void setActCode(String actCode) {
            this.actCode = actCode;
        }

        public String getSectionCode() {
            return sectionCode;
        }

        public void setSectionCode(String sectionCode) {
            this.sectionCode = sectionCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ActSectionAssociationId that = (ActSectionAssociationId) o;
            return Objects.equals(caseMasterId, that.caseMasterId)
                    && Objects.equals(actCode, that.actCode)
                    && Objects.equals(sectionCode, that.sectionCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(caseMasterId, actCode, sectionCode);
        }
    }
}
