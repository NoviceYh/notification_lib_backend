package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.exception.error.ValidationErrorCode;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.NotificationRequest;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.validation.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class EmailChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(EmailChannelSender.class);

    private final EmailProviderPort emailProvider;
    private final EmailValidator validator;

    public EmailChannelSender(EmailProviderPort emailProvider, EmailValidator validator) {
        this.emailProvider = Objects.requireNonNull(emailProvider, "emailProvider es obligatorio");
        this.validator = Objects.requireNonNull(validator, "validator es obligatorio");
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        // 1) Validaciones “comunes” de request (canal, recipients, etc.)
        if (request == null) {
            log.error("La notificación no puede ser null");
            throw new IllegalArgumentException("La notificación no puede ser null");
        }
        if (request.channel() != Channel.EMAIL) {
            log.error("Canal inválido: se esperaba EMAIL pero se recibió {}", request.channel());
            throw new IllegalArgumentException("EmailChannelSender solo acepta canal EMAIL");
        }

        // 2) Validación y casteo del payload
        EmailPayload payload = extractEmailPayload(request);

        // 3) Validaciones de negocio (subject/body, emails, etc.)
        validator.validate(request.recipients(), payload);

        // 4) Envío usando el PORT (hexagonal)
        try {
            return emailProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            // ya viene tipada, se propaga
            log.error("Error de entrega enviando email: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // envolvemos cualquier error inesperado en DeliveryException
            //todo loguear excepcion original para debugging
            log.error("Error inesperado enviando email: {}", e.getMessage(), e);
            throw new DeliveryException("Fallo inesperado enviando email");
        }
    }

    private EmailPayload extractEmailPayload(NotificationRequest request) {
        if (request.payload() == null) {
            log.error("El payload no puede ser null para canal EMAIL");
            throw new ValidationException(ValidationErrorCode.PAYLOAD_EMPTY);
        }
        if (!(request.payload() instanceof EmailPayload emailPayload)) {
            log.error("Payload inválido: se esperaba EmailPayload pero se recibió {}", request.payload().getClass());
            throw new ValidationException(ValidationErrorCode.PAYLOAD_EMPTY);
        }
        return emailPayload;
    }
}
