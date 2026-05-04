package com.cenimarket.backend.global.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.BindingResult;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        String code,
        String message,
        List<FieldError> errors
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message, List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                FieldError.from(bindingResult)
        );
    }

    public record FieldError(
            String field,
            String message
    ) {

        private static List<FieldError> from(BindingResult bindingResult) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                    .toList();
        }
    }
}
