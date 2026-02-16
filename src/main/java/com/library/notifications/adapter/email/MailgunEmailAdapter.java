package com.library.notifications.adapter.email;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Provider;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.EmailProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;

public class MailgunEmailAdapter implements EmailProviderPort {

    private final MailgunConfig config;

    public MailgunEmailAdapter(MailgunConfig config) {
        this.config = Objects.requireNonNull(config, "config is required");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, EmailPayload payload) {
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }

        // Simulate a provider message ID
        String providerMessageId = "mg-" + UUID.randomUUID();

        return DeliveryResult.success(
                Provider.MAILGUN.name(),
                providerMessageId,
                Instant.now()
        );
    }

    public record MailgunConfig(String apiKey, String domain, String fromEmail) {}
}
