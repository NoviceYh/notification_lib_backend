package adapter.email.mailgun;

import api.exception.DeliveryException;
import api.model.DeliveryResult;
import api.model.EmailPayload;
import api.model.Recipient;
import core.port.EmailProviderPort;

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
            throw new DeliveryException("Mailgun: apiKey inválida");
        }

        String providerMessageId = "mg-" + UUID.randomUUID();

        return DeliveryResult.success(
                "mailgun",
                providerMessageId,
                Instant.now()
        );
    }

    public record MailgunConfig(String apiKey, String domain) {}
}
