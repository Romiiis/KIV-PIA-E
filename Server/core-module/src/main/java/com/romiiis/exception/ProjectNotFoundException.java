package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when a project is not found in the system.
 *
 * @author Roman Pejs
 */
public class ProjectNotFoundException extends BaseException {

    public ProjectNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
