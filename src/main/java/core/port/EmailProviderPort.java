package core.port;

import api.model.DeliveryResult;
import api.model.EmailPayload;
import api.model.Recipient;

import java.util.List;

public interface EmailProviderPort {
    DeliveryResult send(List<Recipient> recipients, EmailPayload payload);
}
