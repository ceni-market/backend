package com.cenimarket.backend.global.error;

import com.cenimarket.backend.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, exception.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, exception.getBindingResult());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception
    ) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, "이미지는 개당 5MB 이하만 업로드할 수 있습니다.");

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMultipartException(MultipartException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, "파일 업로드 요청이 올바르지 않습니다.");

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, exception.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception exception) {
        log.error("Unhandled exception occurred", exception);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorResponse.code(), errorResponse.message(), errorResponse));
    }

    /**
     * 🔍 스프링 부트 3 정적 리소스 또는 매핑되지 않은 URL 요청(404) 처리
     * 크롬 데브툴 노이즈나 잘못된 경로 요청이 에러 로그(ERROR Stack Trace)를 더럽히지 않도록 방어합니다.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {
        // 거대한 스택 트레이스 없이, 어떤 주소에서 404가 났는지 한 줄만 깔끔하게 기록합니다.
        log.warn("Missing Resource (404 Not Found): {}", e.getMessage());

        // 클라이언트에게는 깔끔하게 404 상태코드만 반환합니다.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }
}
