package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.ArrestSurrender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArrestSurrenderRepository extends JpaRepository<ArrestSurrender, Integer> {

    List<ArrestSurrender> findByCaseMasterId(Integer caseMasterId);
}
