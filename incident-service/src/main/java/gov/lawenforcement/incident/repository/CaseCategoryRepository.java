package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.CaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseCategoryRepository extends JpaRepository<CaseCategory, Integer> {
}
