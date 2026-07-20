package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.InvOccuranceTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvOccuranceTimeRepository extends JpaRepository<InvOccuranceTime, Integer> {

    Optional<InvOccuranceTime> findByCaseMasterId(Integer caseMasterId);
}
