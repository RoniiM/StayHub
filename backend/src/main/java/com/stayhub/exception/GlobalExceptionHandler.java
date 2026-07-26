package com.stayhub.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields",
                validationErrors, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                         HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    @ExceptionHandler(PropertyOwnershipException.class)
    public ResponseEntity<ErrorResponse> handlePropertyOwnershipException(PropertyOwnershipException ex,
                                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null, request);
    }

    @ExceptionHandler(BookingOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleBookingOwnershipException(BookingOwnershipException ex,
                                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null, request);
    }

    @ExceptionHandler(InvalidBookingRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingRequestException(InvalidBookingRequestException ex,
                                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null, request);
    }

    @ExceptionHandler(BookingOverlapException.class)
    public ResponseEntity<ErrorResponse> handleBookingOverlapException(BookingOverlapException ex,
                                                                         HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null, request);
    }

    @ExceptionHandler(InvalidBookingStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingStatusTransitionException(
            InvalidBookingStatusTransitionException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), null, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                                                                                 HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body", null, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                                     HttpServletRequest request) {
        String message = "Invalid value '%s' for parameter '%s'".formatted(ex.getValue(), ex.getName());
        return buildResponse(HttpStatus.BAD_REQUEST, message, null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null, request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message,
                                                          Map<String, String> validationErrors,
                                                          HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                validationErrors,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}
