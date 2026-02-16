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

        // Simulate provider rejection (e.g. destination not reachable)
        if (recipients.stream().anyMatch(r -> r.value().endsWith("000"))) {
            return DeliveryResult.failed(
                    "twilio",
                    Instant.now(),
                    "destination not reachable"
            );
        }

        // Simulate a provider message ID
        String providerMessageId = "tw-" + UUID.randomUUID();

        return DeliveryResult.success(
                "twilio",
                providerMessageId,
                Instant.now()
        );
    }

    public record TwilioConfig(String accountSid, String authToken, String fromPhoneNumber) {}
}
