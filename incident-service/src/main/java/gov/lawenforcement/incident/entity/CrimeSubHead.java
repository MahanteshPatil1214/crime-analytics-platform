package gov.lawenforcement.incident.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crime_sub_head")
public class CrimeSubHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crime_sub_head_id")
    private Integer crimeSubHeadId;

    @Column(name = "crime_head_id", nullable = false)
    private Integer crimeHeadId;

    @Column(name = "crime_head_name", nullable = false, length = 200)
    private String crimeHeadName;

    @Column(name = "seq_id")
    private Integer seqId;

    public Integer getCrimeSubHeadId() {
        return crimeSubHeadId;
    }

    public void setCrimeSubHeadId(Integer crimeSubHeadId) {
        this.crimeSubHeadId = crimeSubHeadId;
    }

    public Integer getCrimeHeadId() {
        return crimeHeadId;
    }

    public void setCrimeHeadId(Integer crimeHeadId) {
        this.crimeHeadId = crimeHeadId;
    }

    public String getCrimeHeadName() {
        return crimeHeadName;
    }

    public void setCrimeHeadName(String crimeHeadName) {
        this.crimeHeadName = crimeHeadName;
    }

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }
}
