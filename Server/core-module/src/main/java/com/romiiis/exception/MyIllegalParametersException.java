package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

public class MyIllegalParametersException extends BaseException {

    public MyIllegalParametersException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}
