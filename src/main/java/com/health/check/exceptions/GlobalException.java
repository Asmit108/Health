package com.health.check.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for centralized error handling across the application.
 *
 * This controller advice intercepts exceptions thrown throughout the application
 * and returns standardized error responses with appropriate HTTP status codes.
 *
 * @author Health Check Team
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Handles AccessDeniedException when a user lacks required permissions.
     *
     * @param e the exception that was thrown
     * @param request the HTTP request that triggered the exception
     * @return ResponseEntity with 403 Forbidden status and error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> AccessDeniedException(Exception e, HttpServletRequest request) {
        // Create standardized error response with access denial details
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCause(String.valueOf(e.getCause()));
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());
        errorDetails.setDetails(request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    /**
     * Handles BadCredentialsException during authentication failures.
     *
     * @param e the exception that was thrown
     * @param request the HTTP request that triggered the exception
     * @return ResponseEntity with 401 Unauthorized status and error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> BadCredentialsException(Exception e, HttpServletRequest request) {
        // Create standardized error response with authentication failure details
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCause(String.valueOf(e.getCause()));
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());
        errorDetails.setDetails(request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    /**
     * Handles AlreadyExistsException when duplicate resources are created.
     *
     * @param e the exception that was thrown
     * @param request the HTTP request that triggered the exception
     * @return ResponseEntity with 409 Conflict status and error details
     */
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> AlreadyExistsException(Exception e, HttpServletRequest request) {
        // Create standardized error response with resource conflict details
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCause(String.valueOf(e.getCause()));
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());
        errorDetails.setDetails(request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    /**
     * Handles NotFoundException when requested resources are not found.
     *
     * @param e the exception that was thrown
     * @param request the HTTP request that triggered the exception
     * @return ResponseEntity with 404 Not Found status and error details
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetails> NotFoundException(Exception e, HttpServletRequest request) {
        // Create standardized error response with resource not found details
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCause(String.valueOf(e.getCause()));
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());
        errorDetails.setDetails(request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    /**
     * Handles ResponseParseException and generic Exceptions.
     *
     * Serves as a catch-all for any unhandled exceptions in the system.
     *
     * @param e the exception that was thrown
     * @param request the HTTP request that triggered the exception
     * @return ResponseEntity with 500 Internal Server Error status and error details
     */
    @ExceptionHandler(value = {ResponseParseException.class, Exception.class})
    public ResponseEntity<ErrorDetails> ResponseParseAndOtherException(Exception e, HttpServletRequest request) {
        // Create standardized error response with server error details
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setCause(String.valueOf(e.getCause()));
        errorDetails.setMessage(e.getMessage());
        errorDetails.setTimestamp(LocalDateTime.now());
        errorDetails.setDetails(request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
