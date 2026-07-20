package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Accused;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccusedRepository extends JpaRepository<Accused, Integer> {

    List<Accused> findByCaseMasterId(Integer caseMasterId);
}
