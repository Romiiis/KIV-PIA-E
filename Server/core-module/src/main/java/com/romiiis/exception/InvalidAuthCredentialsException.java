package com.romiiis.exception;


import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when invalid credentials are provided during authentication
 * For example email format is incorrect or password does not meet security criteria.
 * For login, this exception is thrown when the email does not exist or the password is incorrect.
 *
 * @author Roman Pejs
 */
public class InvalidAuthCredentialsException extends BaseException {
    public InvalidAuthCredentialsException() {
        super("Invalid data provided for authentication.", ErrorCode.UNAUTHORIZED);
    }
}
