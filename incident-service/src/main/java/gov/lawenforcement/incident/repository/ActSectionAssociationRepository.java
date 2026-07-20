package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.ActSectionAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActSectionAssociationRepository extends JpaRepository<ActSectionAssociation, ActSectionAssociation.ActSectionAssociationId> {

    List<ActSectionAssociation> findByCaseMasterId(Integer caseMasterId);
}
