package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.InvArrestSurrenderAccused;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvArrestSurrenderAccusedRepository extends JpaRepository<InvArrestSurrenderAccused, InvArrestSurrenderAccused.InvArrestSurrenderAccusedId> {

    List<InvArrestSurrenderAccused> findByArrestSurrenderId(Integer arrestSurrenderId);
}
