package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.NotificationRequest;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.validation.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.UNEXPECTED_ERROR;
import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class EmailChannelSender implements ChannelSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailChannelSender.class);
    private final EmailProviderPort emailProvider;
    private final EmailValidator validator;

    public EmailChannelSender(EmailProviderPort emailProvider, EmailValidator validator) {
        this.emailProvider = Objects.requireNonNull(emailProvider, "emailProvider is required");
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        if (request == null) {
            throw new ValidationException(REQUEST_NULL);
        }
        if (request.channel() != Channel.EMAIL) {
            String value = request.channel() == null ? "null" : request.channel().name();
            throw new ValidationException(INVALID_CHANNEL, Channel.EMAIL.name(), value);
        }

        EmailPayload payload = extractEmailPayload(request);
        validator.validate(request.recipients(), payload);

        try {
            logger.debug("Sending email: recipientsCount={}, subjectLength={}, bodyLength={}",
                    request.recipients().size(),
                    payload.subject() == null ? 0 : payload.subject().length(),
                    payload.body() == null ? 0 : payload.body().length());
            return emailProvider.send(request.recipients(), payload);
        } catch (DeliveryException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error sending Email", e);
            throw new DeliveryException(UNEXPECTED_ERROR, e);
        }
    }

    private EmailPayload extractEmailPayload(NotificationRequest request) {
        if (request.payload() == null) {
            throw new ValidationException(EMAIL_PAYLOAD_INVALID);
        }
        if (!(request.payload() instanceof EmailPayload emailPayload)) {
            throw new ValidationException(EMAIL_PAYLOAD_INVALID);
        }
        return emailPayload;
    }
}
