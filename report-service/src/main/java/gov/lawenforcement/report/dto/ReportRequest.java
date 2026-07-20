package gov.lawenforcement.report.dto;

import lombok.Data;

@Data
public class ReportRequest {

    private String firNumber;
    private String title;
    private String description;
    private String severity;
    private String status;
    private String district;
    private String date;
    private String address;
    private String personName;
    private String personType;
    private int convictionCount;
    private double riskScore;
    private String charges;
}
