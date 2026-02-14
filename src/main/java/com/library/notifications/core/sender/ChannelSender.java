package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;

public interface ChannelSender {

    /**
     * @return el canal que este sender soporta (EMAIL, SMS, PUSH)
     */
    Channel channel();

    /**
     * Envía la notificación del canal correspondiente.
     *
     * @throws ValidationException si la request/payload es inválida
     * @throws DeliveryException si el proveedor falla al enviar
     */
    DeliveryResult send(NotificationRequest request);
}
