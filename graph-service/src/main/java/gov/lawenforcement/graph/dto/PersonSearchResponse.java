package gov.lawenforcement.graph.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PersonSearchResponse {
    private String query;
    private List<PersonSearchResult> results;
    private int count;

    @Data
    @Builder
    public static class PersonSearchResult {
        private String personId;
        private String name;
        private Integer age;
        private String gender;
        private String personType;
    }
}
