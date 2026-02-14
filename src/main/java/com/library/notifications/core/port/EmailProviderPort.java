package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;

public interface EmailProviderPort {
    DeliveryResult send(List<Recipient> recipients, EmailPayload payload);
}
