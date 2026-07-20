package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByUnitId(Integer unitId);

    List<Employee> findByDistrictId(Integer districtId);
}
