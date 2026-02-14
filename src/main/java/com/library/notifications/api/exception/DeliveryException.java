package com.library.notifications.api.exception;

import com.library.notifications.api.exception.error.DeliveryErrorCode;

public class DeliveryException extends RuntimeException {

    private final DeliveryErrorCode errorCode;

    public DeliveryException(DeliveryErrorCode errorCode) {
        super(errorCode.description());
        this.errorCode = errorCode;
    }

    public DeliveryException(DeliveryErrorCode errorCode, Throwable cause) {
        super(errorCode.description(), cause);
        this.errorCode = errorCode;
    }

    public DeliveryErrorCode getErrorCode() {
        return errorCode;
    }
}
