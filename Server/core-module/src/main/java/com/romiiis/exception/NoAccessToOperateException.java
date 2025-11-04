package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when a user does not have access to perform a certain operation.
 *
 * @author Roman Pejs
 */
public class NoAccessToOperateException extends BaseException {

    public NoAccessToOperateException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }
}
