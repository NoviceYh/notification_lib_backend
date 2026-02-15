package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.PushPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;

public interface PushProviderPort {
    DeliveryResult send(List<Recipient> recipients, PushPayload payload);

}
