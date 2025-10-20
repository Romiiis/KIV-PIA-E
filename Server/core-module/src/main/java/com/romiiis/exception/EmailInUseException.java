package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when an email is already in use.
 *
 * @author Roman Pejs
 */
public class EmailInUseException extends BaseException {
    public EmailInUseException() {
        super("Email is already in use", ErrorCode.CONFLICT);
    }
}
