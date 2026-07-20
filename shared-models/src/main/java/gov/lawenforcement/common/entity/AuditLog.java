package gov.lawenforcement.common.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_audit_timestamp", columnList = "action_timestamp")
})
public class AuditLog {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "user_role", length = 50)
    private String userRole;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "request_uri", length = 500)
    private String requestUri;

    @Column(name = "old_value_hash", length = 64)
    private String oldValueHash;

    @Column(name = "new_value_hash", length = 64)
    private String newValueHash;

    @Column(name = "action_timestamp", nullable = false)
    private Instant actionTimestamp;

    @Column(name = "tamper_seal", nullable = false, length = 128)
    private String tamperSeal;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
        this.actionTimestamp = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String requestUri) { this.requestUri = requestUri; }
    public String getTamperSeal() { return tamperSeal; }
    public void setTamperSeal(String tamperSeal) { this.tamperSeal = tamperSeal; }
    public Instant getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(Instant actionTimestamp) { this.actionTimestamp = actionTimestamp; }
}
