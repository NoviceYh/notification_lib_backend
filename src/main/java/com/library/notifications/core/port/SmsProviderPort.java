package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.api.model.SmsPayload;

import java.util.List;

public interface SmsProviderPort {
    DeliveryResult send(List<Recipient> recipients, SmsPayload payload);
}
