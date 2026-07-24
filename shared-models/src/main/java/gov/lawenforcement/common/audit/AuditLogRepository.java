package gov.lawenforcement.common.audit;

import gov.lawenforcement.common.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUserIdOrderByActionTimestampDesc(String userId);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByActionTimestampDesc(String entityType, String entityId);

    List<AuditLog> findByActionOrderByActionTimestampDesc(String action);

    @Query("SELECT a FROM AuditLog a WHERE a.actionTimestamp BETWEEN :from AND :to ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findByDateRange(@Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId AND a.actionTimestamp BETWEEN :from AND :to ORDER BY a.actionTimestamp DESC")
    List<AuditLog> findByEntityTypeAndEntityIdAndDateRange(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("from") Instant from,
            @Param("to") Instant to);
}
