package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankRepository extends JpaRepository<Rank, Integer> {
}
