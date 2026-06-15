package com.health.check.exceptions;

/**
 * Custom exception thrown when a requested entity is not found in the database.
 *
 * This exception is used throughout the application to indicate that a read or
 * lookup operation failed because the targeted resource does not exist.
 *
 * @author Health Check Team
 * @version 1.0
 */
public class ResponseParseException extends RuntimeException {
    /**
     * Constructs a ResponseParseException with a specific error message.
     *
     * @param message detailed message describing what failed during parsing
     */
    public ResponseParseException(String message) {
        super(message);
    }
}
