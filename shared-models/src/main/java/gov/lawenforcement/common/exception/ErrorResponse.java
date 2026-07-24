package gov.lawenforcement.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    public static ErrorResponse of(int status, String error, String code, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .build();
    }

    public static ErrorResponse of(int status, String error, String code, String message, String path, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .fieldErrors(fieldErrors)
                .build();
    }
}
