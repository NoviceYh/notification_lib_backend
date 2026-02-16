package com.library.notifications.adapter.email;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Provider;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.EmailProviderPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;

public class SendGridEmailAdapter implements EmailProviderPort {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailAdapter.class);
    private final SendGridConfig config;

    public SendGridEmailAdapter(SendGridConfig config) {
        this.config = Objects.requireNonNull(config, "config is required");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, EmailPayload payload) {
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }

        // Simulate provider rejection (e.g. rate limit or blocked recipient)
        if (recipients != null && !recipients.isEmpty() && recipients.size() > 50) {
            return DeliveryResult.failed(
                    Provider.SENDGRID.name(),
                    Instant.now(),
                    "rate limited by provider"
            );
        }

        // Simulate a provider message ID
        String providerMessageId = "sg-" + UUID.randomUUID();

        logger.debug(
                "Sendgrid email sent successfully. providerMessageId={}",
                providerMessageId
        );

        return DeliveryResult.success(
                Provider.SENDGRID.name(),
                providerMessageId,
                Instant.now()
        );
    }

    public record SendGridConfig(String apiKey, String fromEmail) {}

}
