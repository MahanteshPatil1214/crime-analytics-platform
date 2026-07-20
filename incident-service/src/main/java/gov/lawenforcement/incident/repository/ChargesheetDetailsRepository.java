package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.ChargesheetDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChargesheetDetailsRepository extends JpaRepository<ChargesheetDetails, Integer> {

    List<ChargesheetDetails> findByCaseMasterId(Integer caseMasterId);
}
