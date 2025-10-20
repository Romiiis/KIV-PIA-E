package com.romiiis.exception;

import com.romiiis.configuration.ErrorCode;

/**
 * Exception thrown when feedback is not found.
 *
 * @author Roman Pejs
 */
public class FeedbackNotFoundException extends BaseException {
    public FeedbackNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
