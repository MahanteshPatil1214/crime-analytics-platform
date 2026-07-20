package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

    List<Unit> findByDistrictId(Integer districtId);
}
