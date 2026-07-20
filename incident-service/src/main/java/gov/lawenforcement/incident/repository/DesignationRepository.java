package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignationRepository extends JpaRepository<Designation, Integer> {
}
