package com.health.check.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for standardized error response details.
 * This class is used to structure exception information that is returned to clients
 * in error responses, providing a consistent and informative error format.
 *
 * @author Health Check Team
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String message;
    private String cause;
    private String details;
    private LocalDateTime timestamp;
}
