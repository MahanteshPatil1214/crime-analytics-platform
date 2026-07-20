package gov.lawenforcement.person.repository;

import gov.lawenforcement.person.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    List<Person> findByPersonType(Person.PersonType personType);

    List<Person> findByLastNameContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
        String lastName, String firstName);

    List<Person> findByIsKnownOffender(Boolean isKnownOffender);

    Page<Person> findByIsKnownOffender(Boolean isKnownOffender, Pageable pageable);

    List<Person> findByRiskScoreBetween(Double min, Double max);

    long countByPersonType(Person.PersonType personType);

    @Query(value = """
        SELECT * FROM persons p
        WHERE (:name IS NULL OR :name = ''
               OR p.first_name ILIKE ('%' || :name || '%')
               OR p.last_name ILIKE ('%' || :name || '%'))
          AND (:personType IS NULL OR p.person_type = :personType)
          AND (:minRisk IS NULL OR p.risk_score >= :minRisk)
          AND (:maxRisk IS NULL OR p.risk_score <= :maxRisk)
        ORDER BY p.created_at DESC
        """, countQuery = """
        SELECT count(*) FROM persons p
        WHERE (:name IS NULL OR :name = ''
               OR p.first_name ILIKE ('%' || :name || '%')
               OR p.last_name ILIKE ('%' || :name || '%'))
          AND (:personType IS NULL OR p.person_type = :personType)
          AND (:minRisk IS NULL OR p.risk_score >= :minRisk)
          AND (:maxRisk IS NULL OR p.risk_score <= :maxRisk)
        """, nativeQuery = true)
    Page<Person> search(
        @Param("name") String name,
        @Param("personType") String personType,
        @Param("minRisk") Double minRisk,
        @Param("maxRisk") Double maxRisk,
        Pageable pageable);

    @Query("""
        SELECT p.personType, count(p)
        FROM Person p
        GROUP BY p.personType
        ORDER BY count(p) DESC
        """)
    List<Object[]> countGroupedByPersonType();
}
