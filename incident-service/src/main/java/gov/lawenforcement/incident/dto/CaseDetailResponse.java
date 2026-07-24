package gov.lawenforcement.incident.dto;

import gov.lawenforcement.incident.entity.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CaseDetailResponse {
    private CaseMaster caseInfo;
    private List<ComplainantDetails> complainants;
    private List<Victim> victims;
    private List<Accused> accused;
    private List<ArrestSurrender> arrests;
    private List<ActSectionAssociation> actSections;
    private List<ChargesheetDetails> chargesheets;
    private InvOccuranceTime occurrenceTime;
}
