package gov.lawenforcement.incident.repository;

import gov.lawenforcement.incident.entity.CaseMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaseMasterRepository extends JpaRepository<CaseMaster, Integer>, JpaSpecificationExecutor<CaseMaster> {

    Page<CaseMaster> findAll(Pageable pageable);

    Optional<CaseMaster> findByCrimeNo(String crimeNo);

    Page<CaseMaster> findByCaseStatusId(Integer statusId, Pageable pageable);

    List<CaseMaster> findByPoliceStationId(Integer stationId);

    List<CaseMaster> findByCrimeMajorHeadId(Integer headId);

    @Query(value = """
        SELECT cm.* FROM case_master cm
        LEFT JOIN unit u ON cm.police_station_id = u.unit_id
        LEFT JOIN district d ON u.district_id = d.district_id
        LEFT JOIN crime_head ch ON cm.crime_major_head_id = ch.crime_head_id
        WHERE (:district IS NULL OR d.district_name ILIKE CONCAT('%', :district, '%'))
        AND (:statusId IS NULL OR cm.case_status_id = :statusId)
        AND (:crimeHeadId IS NULL OR cm.crime_major_head_id = :crimeHeadId)
        AND (:crimeNo IS NULL OR cm.crime_no ILIKE CONCAT('%', :crimeNo, '%'))
        AND (:startDate IS NULL OR cm.crime_registered_date >= CAST(:startDate AS date))
        AND (:endDate IS NULL OR cm.crime_registered_date <= CAST(:endDate AS date))
        ORDER BY cm.crime_registered_date DESC
        """, countQuery = """
        SELECT COUNT(*) FROM case_master cm
        LEFT JOIN unit u ON cm.police_station_id = u.unit_id
        LEFT JOIN district d ON u.district_id = d.district_id
        WHERE (:district IS NULL OR d.district_name ILIKE CONCAT('%', :district, '%'))
        AND (:statusId IS NULL OR cm.case_status_id = :statusId)
        AND (:crimeHeadId IS NULL OR cm.crime_major_head_id = :crimeHeadId)
        AND (:crimeNo IS NULL OR cm.crime_no ILIKE CONCAT('%', :crimeNo, '%'))
        AND (:startDate IS NULL OR cm.crime_registered_date >= CAST(:startDate AS date))
        AND (:endDate IS NULL OR cm.crime_registered_date <= CAST(:endDate AS date))
        """, nativeQuery = true)
    Page<CaseMaster> search(
            @Param("district") String district,
            @Param("statusId") Integer statusId,
            @Param("crimeHeadId") Integer crimeHeadId,
            @Param("crimeNo") String crimeNo,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            Pageable pageable);
}
