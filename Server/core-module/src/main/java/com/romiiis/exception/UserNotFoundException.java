package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when a user is not found in the system.
 */
public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
