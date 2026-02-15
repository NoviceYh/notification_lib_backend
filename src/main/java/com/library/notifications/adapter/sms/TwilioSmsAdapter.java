package com.library.notifications.adapter.sms;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.api.model.SmsPayload;
import com.library.notifications.core.port.SmsProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;

public class TwilioSmsAdapter implements SmsProviderPort {

    private final TwilioConfig config;

    public TwilioSmsAdapter(TwilioConfig config) {
        this.config = Objects.requireNonNull(config, "config is required");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, SmsPayload payload) {
        if (config.accountSid() == null || config.accountSid().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }
        if (config.authToken() == null || config.authToken().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }

        // Simulate a provider message ID
        String providerMessageId = "tw-" + UUID.randomUUID();

        return DeliveryResult.success(
                "twilio",
                providerMessageId,
                Instant.now()
        );
    }

    public record TwilioConfig(String accountSid, String authToken) {}
}
