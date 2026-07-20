package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.ComplainantDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplainantDetailsRepository extends JpaRepository<ComplainantDetails, Integer> {

    List<ComplainantDetails> findByCaseMasterId(Integer caseMasterId);
}
