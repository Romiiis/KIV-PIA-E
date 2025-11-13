package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when a user attempts to log in with OAuth but the account is already logged in using OAuth.
 */
public class LoggedByDifferentMethodException extends BaseException {

    public LoggedByDifferentMethodException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}
