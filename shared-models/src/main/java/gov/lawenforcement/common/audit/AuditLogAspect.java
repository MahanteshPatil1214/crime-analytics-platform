package gov.lawenforcement.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.lawenforcement.common.crypto.TamperEvidenceUtil;
import gov.lawenforcement.common.entity.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(gov.lawenforcement.common.audit.Auditable)")
    public void auditableMethods() {}

    @Around("auditableMethods()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception ex) {
            exception = ex;
            throw ex;
        } finally {
            try {
                saveAuditLog(joinPoint, startTime, result, exception);
            } catch (Exception auditEx) {
                log.error("Failed to save audit log: {}", auditEx.getMessage());
            }
        }
    }

    private void saveAuditLog(ProceedingJoinPoint joinPoint, Instant startTime,
                              Object result, Exception exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Auditable auditable = method.getAnnotation(Auditable.class);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(resolveUserId());
        auditLog.setUserRole(resolveUserRole());
        auditLog.setAction(auditable.action());
        auditLog.setEntityType(auditable.entityType());
        auditLog.setEntityId(resolveEntityId(joinPoint));
        auditLog.setDescription(buildDescription(auditable, result, exception));
        auditLog.setIpAddress(resolveIpAddress());
        auditLog.setRequestUri(resolveRequestUri());

        if (exception == null && result != null) {
            auditLog.setNewValueHash(computeHash(result));
        }

        String previousSeal = "GENESIS";
        try {
            auditLog.setTamperSeal(
                    TamperEvidenceUtil.computeChainedSeal(
                            previousSeal,
                            auditLog.getUserId(),
                            auditLog.getAction(),
                            auditLog.getEntityType(),
                            auditLog.getEntityId(),
                            startTime
                    )
            );
        } catch (Exception e) {
            auditLog.setTamperSeal(" SEAL_FAILED");
            log.warn("Failed to compute tamper seal: {}", e.getMessage());
        }

        auditLogRepository.save(auditLog);
        log.debug("Audit log saved: action={}, entityType={}, entityId={}, user={}",
                auditLog.getAction(), auditLog.getEntityType(),
                auditLog.getEntityId(), auditLog.getUserId());
    }

    private String resolveUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "SYSTEM";
    }

    private String resolveUserRole() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
                return auth.getAuthorities().iterator().next().getAuthority();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String resolveEntityId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Number || arg instanceof String) {
                    return String.valueOf(arg);
                }
                if (arg instanceof UUID) {
                    return arg.toString();
                }
            }
        }
        return null;
    }

    private String buildDescription(Auditable auditable, Object result, Exception exception) {
        StringBuilder desc = new StringBuilder();
        if (auditable.description().isEmpty()) {
            desc.append(auditable.action()).append(" ").append(auditable.entityType());
        } else {
            desc.append(auditable.description());
        }
        if (exception != null) {
            desc.append(" [FAILED: ").append(exception.getMessage()).append("]");
        }
        return desc.toString();
    }

    private String resolveIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isBlank()) {
                    return xff.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String resolveRequestUri() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                return request.getMethod() + " " + request.getRequestURI();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String computeHash(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "HASH_FAILED";
        }
    }
}
