package com.library.notifications.adapter.push;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.PushPayload;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.core.port.PushProviderPort;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;

public class FirebasePushAdapter implements PushProviderPort {

    private final FirebaseConfig config;

    public FirebasePushAdapter(FirebaseConfig config) {
        this.config = Objects.requireNonNull(config, "config is required");
    }

    @Override
    public DeliveryResult send(List<Recipient> recipients, PushPayload payload) {
        if (config.projectId() == null || config.projectId().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }
        if (config.serviceAccountKey() == null || config.serviceAccountKey().isBlank()) {
            throw new DeliveryException(PROVIDER_AUTH_ERROR);
        }

        // Simulate a provider message ID
        String providerMessageId = "fb-" + UUID.randomUUID();

        return DeliveryResult.success(
                "firebase",
                providerMessageId,
                Instant.now()
        );
    }

    public record FirebaseConfig(String projectId, String serviceAccountKey) {}
}
