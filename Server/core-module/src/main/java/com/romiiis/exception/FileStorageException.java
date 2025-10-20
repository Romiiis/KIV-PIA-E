package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when there is an error during file storage operations.
 *
 * @author Roman Pejs
 */
public class FileStorageException extends BaseException {
    public FileStorageException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);

    }
}
