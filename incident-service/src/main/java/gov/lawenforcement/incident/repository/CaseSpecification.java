package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.CaseMaster;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CaseSpecification {

    public static Specification<CaseMaster> filterBy(String crimeNo, String briefFacts, Integer caseStatusId, String startDate, String endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (crimeNo != null && !crimeNo.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("crimeNo")), "%" + crimeNo.toLowerCase() + "%"));
            }

            if (briefFacts != null && !briefFacts.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("briefFacts")), "%" + briefFacts.toLowerCase() + "%"));
            }

            if (caseStatusId != null) {
                predicates.add(cb.equal(root.get("caseStatusId"), caseStatusId));
            }

            if (startDate != null && !startDate.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("crimeRegisteredDate"), LocalDate.parse(startDate)));
            }

            if (endDate != null && !endDate.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("crimeRegisteredDate"), LocalDate.parse(endDate)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
