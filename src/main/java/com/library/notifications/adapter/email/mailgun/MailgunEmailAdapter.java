package com.library.notifications.adapter.email.mailgun;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.EmailProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MailgunEmailAdapter implements EmailProviderPort {

    private final MailgunConfig config;

    public MailgunEmailAdapter(MailgunConfig config) {
        this.config = Objects.requireNonNull(config, "config es obligatoria");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, EmailPayload payload) {
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            throw new IllegalArgumentException("Mailgun: apiKey inválida");
        }

        // Simulate a provider message ID
        String providerMessageId = "mg-" + UUID.randomUUID();

        return DeliveryResult.success(
                "mailgun",
                providerMessageId,
                Instant.now()
        );
    }

    public record MailgunConfig(String apiKey, String domain) {}
}
