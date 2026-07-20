package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Victim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VictimRepository extends JpaRepository<Victim, Integer> {

    List<Victim> findByCaseMasterId(Integer caseMasterId);
}
