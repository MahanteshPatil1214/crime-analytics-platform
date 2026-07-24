package gov.lawenforcement.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutocompleteItem {
    private String text;
    private String type;
    private String id;
}
