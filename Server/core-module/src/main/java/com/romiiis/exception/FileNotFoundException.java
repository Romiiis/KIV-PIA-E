package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when a requested file is not found.
 *
 * @author Roman Pejs
 */
public class FileNotFoundException extends BaseException {
    public FileNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
