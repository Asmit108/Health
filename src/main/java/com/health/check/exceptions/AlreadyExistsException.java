package com.health.check.exceptions;

/**
 * Custom exception thrown when an attempt is made to create a duplicate entity.
 * This exception is used to indicate that an operation failed because a resource
 * with duplicate data already exists (e.g., duplicate email, duplicate appointment time).
 *
 * @author Health Check Team
 * @version 1.0
 */
public class AlreadyExistsException extends Exception {
    /**
     * Constructs an AlreadyExistsException with a specific error message.
     *
     * @param message detailed message describing what already exists
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}
