package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;
import lombok.Getter;

/**
 * Base exception class for custom exceptions in the application.
 *
 * @author Roman Pejs
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private ErrorCode httpStatus = ErrorCode.INTERNAL_SERVER_ERROR;

    public BaseException(String message, ErrorCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BaseException(String message) {
        super(message);
    }

}
