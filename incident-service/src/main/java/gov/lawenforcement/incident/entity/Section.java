package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "section")
@IdClass(Section.SectionId.class)
public class Section {

    @Id
    @Column(name = "act_code", length = 20)
    private String actCode;

    @Id
    @Column(name = "section_code", length = 20)
    private String sectionCode;

    @Column(name = "section_description", length = 500)
    private String sectionDescription;

    @Column(name = "active", columnDefinition = "BIT")
    private Boolean active;

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

    public String getSectionDescription() {
        return sectionDescription;
    }

    public void setSectionDescription(String sectionDescription) {
        this.sectionDescription = sectionDescription;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public static class SectionId implements Serializable {

        private String actCode;
        private String sectionCode;

        public SectionId() {
        }

        public SectionId(String actCode, String sectionCode) {
            this.actCode = actCode;
            this.sectionCode = sectionCode;
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
            SectionId that = (SectionId) o;
            return Objects.equals(actCode, that.actCode)
                    && Objects.equals(sectionCode, that.sectionCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(actCode, sectionCode);
        }
    }
}
