package com.library.notifications.adapter.email.sendgrid;


import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.EmailProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SendGridEmailAdapter implements EmailProviderPort {

    private final SendGridConfig config;

    public SendGridEmailAdapter(SendGridConfig config) {
        this.config = Objects.requireNonNull(config, "config es obligatoria");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, EmailPayload payload) {
        // Simulación: podrías “fallar” si apiKey está vacía, por ejemplo.
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            throw new IllegalArgumentException("SendGrid: apiKey inválida");
        }

        // Simular un ID de mensaje del proveedor
        String providerMessageId = "sg-" + UUID.randomUUID();

        return DeliveryResult.success(
                "sendgrid",
                providerMessageId,
                Instant.now()
        );
    }

    public record SendGridConfig(String apiKey) {}

}
