package core.sender;

import api.exception.DeliveryException;
import api.exception.ValidationException;
import api.model.Channel;
import api.model.DeliveryResult;
import api.model.NotificationRequest;

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
