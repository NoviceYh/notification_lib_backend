package com.library.notifications.adapter.email;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.EmailProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;

public class SendGridEmailAdapter implements EmailProviderPort {

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
        if (recipients.size() > 50) {
            return DeliveryResult.failed(
                    "sendgrid",
                    Instant.now(),
                    "rate limited by provider"
            );
        }

        // Simulate a provider message ID
        String providerMessageId = "sg-" + UUID.randomUUID();

        return DeliveryResult.success(
                "sendgrid",
                providerMessageId,
                Instant.now()
        );
    }

    public record SendGridConfig(String apiKey, String fromEmail) {}

}
