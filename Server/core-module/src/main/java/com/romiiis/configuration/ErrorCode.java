package com.romiiis.configuration;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INTERNAL_ERROR(500),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    CONFLICT(409);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

}
