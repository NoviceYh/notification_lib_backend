package com.library.notifications.api.exception;

import com.library.notifications.api.exception.error.ValidationErrorCode;

public class ValidationException extends RuntimeException {

    private final ValidationErrorCode errorCode;
    private final String[] args;

    public ValidationException(ValidationErrorCode errorCode, String... args) {
        super(args == null || args.length == 0
                ? errorCode.description()
                : errorCode.format((Object) args));
        this.errorCode = errorCode;
        this.args = args;
    }

    public ValidationErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() { return args; }

}
