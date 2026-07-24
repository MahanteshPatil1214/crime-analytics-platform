package gov.lawenforcement.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class FallbackHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FallbackHandler.class);

    private static final String CB_NAME = "reactiveCircuitBreakerName";
    private static final String CB_EXCEPTION = "reactiveCircuitBreakerExecutionException";

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        String circuitBreakerName = exchange.getAttribute(CB_NAME);
        Throwable cause = exchange.getAttribute(CB_EXCEPTION);
        String reason = cause != null ? cause.getMessage() : ex.getMessage();

        log.warn("Circuit breaker '{}' triggered for {}: {}",
                circuitBreakerName, exchange.getRequest().getURI(), reason);

        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        String body = String.format(
                "{\"status\":503,\"error\":\"Service Unavailable\",\"message\":\"Circuit breaker '%s' is open. The service is temporarily unavailable.\",\"requestId\":\"%s\",\"timestamp\":\"%s\"}",
                circuitBreakerName != null ? circuitBreakerName : "unknown",
                requestId != null ? requestId : "unknown",
                Instant.now().toString()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
