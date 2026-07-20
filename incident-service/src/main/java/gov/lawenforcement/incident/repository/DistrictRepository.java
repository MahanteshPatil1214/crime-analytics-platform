package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Integer> {

    List<District> findByStateId(Integer stateId);
}
