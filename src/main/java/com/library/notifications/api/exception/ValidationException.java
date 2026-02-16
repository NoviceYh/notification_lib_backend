package com.library.notifications.api.exception;

import com.library.notifications.api.exception.error.ValidationErrorCode;

public class ValidationException extends RuntimeException {

    private final ValidationErrorCode errorCode;
    private final String[] args;

    public ValidationException(ValidationErrorCode errorCode, String... args) {
        super(buildMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String buildMessage(ValidationErrorCode errorCode, String[] args) {
        if (args == null || args.length == 0) {
            return errorCode.description();
        }
        return errorCode.format(args);
    }


    public ValidationErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getArgs() { return args; }

}
