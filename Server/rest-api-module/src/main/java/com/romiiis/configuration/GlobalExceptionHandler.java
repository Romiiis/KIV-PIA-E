package com.romiiis.configuration;

import com.romiiis.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

/**
 * Global exception handler for REST API.
 * <p>
 * Handles various exceptions and maps them to appropriate HTTP responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.warn("Handled BaseException: {} ({})", ex.getMessage(), ex.getHttpStatus());
        var error = new ErrorResponse(ex.getMessage(), ex.getHttpStatus().getCode());
        return ResponseEntity.status(ex.getHttpStatus().getCode()).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.warn("File upload size exceeded: {}", ex.getMessage());
        var error = new ErrorResponse("File size exceeds the maximum allowed limit", HttpStatus.PAYLOAD_TOO_LARGE.value());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        var error = new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("IO error: {}", ex.getMessage(), ex);
        var error = new ErrorResponse("I/O error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
