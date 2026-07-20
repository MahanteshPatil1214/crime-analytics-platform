package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.CrimeSubHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrimeSubHeadRepository extends JpaRepository<CrimeSubHead, Integer> {

    List<CrimeSubHead> findByCrimeHeadId(Integer crimeHeadId);
}
