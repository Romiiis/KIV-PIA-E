package com.romiiis.configuration;

import lombok.Getter;

/**
 * Enumeration of standard error codes used in the application.
 */
@Getter
public enum ErrorCode {

    BAD_REQUEST(400),
    NOT_FOUND(404),
    CONFLICT(409),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }
}
