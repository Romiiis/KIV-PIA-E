package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private ErrorCode httpStatus = ErrorCode.INTERNAL_ERROR;

    public BaseException(String message, ErrorCode httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
