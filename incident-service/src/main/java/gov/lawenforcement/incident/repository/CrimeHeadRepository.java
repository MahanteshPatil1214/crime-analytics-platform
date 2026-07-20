package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.CrimeHead;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrimeHeadRepository extends JpaRepository<CrimeHead, Integer> {
}
