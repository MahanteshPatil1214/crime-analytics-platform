package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Act;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActRepository extends JpaRepository<Act, String> {
}
