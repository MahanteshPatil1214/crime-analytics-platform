package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.GravityOffence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GravityOffenceRepository extends JpaRepository<GravityOffence, Integer> {
}
