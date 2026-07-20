package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Integer> {
}
