package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Integer> {
    List<Evidence> findByCaseMasterId(Integer caseMasterId);
    void deleteByCaseMasterId(Integer caseMasterId);
}
