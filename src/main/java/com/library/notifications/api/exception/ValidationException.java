package com.library.notifications.api.exception;

import com.library.notifications.api.exception.error.ValidationErrorCode;

public class ValidationException extends RuntimeException {

    private final ValidationErrorCode errorCode;
    private final String details;

    public ValidationException(ValidationErrorCode errorCode) {
        super(errorCode.description());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ValidationException(ValidationErrorCode errorCode, String details) {
        super(details == null || details.isBlank()
                ? errorCode.description()
                : errorCode.description() + " - " + details);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ValidationErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
