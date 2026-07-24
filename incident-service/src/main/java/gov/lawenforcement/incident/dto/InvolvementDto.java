package gov.lawenforcement.incident.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvolvementDto {
    private String type;
    private String name;
    private Integer age;
    private Integer genderId;
    private String personId;
}
