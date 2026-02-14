package com.library.notifications.api.exception;

import com.library.notifications.api.exception.error.DeliveryErrorCode;

public class DeliveryException extends RuntimeException {
    public DeliveryException(String message) {
        super(message);
    }

    public DeliveryException(DeliveryErrorCode errorCode, Throwable cause) {
        super(errorCode.description(), cause);
    }
}
