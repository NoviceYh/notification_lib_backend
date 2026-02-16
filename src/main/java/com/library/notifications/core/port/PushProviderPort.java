package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.PushPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;

public interface PushProviderPort {

    /**
     * Sends a push notification to the specified recipients with the given payload.
     *
     * @param recipients List of recipients to send the push notification to
     * @param payload    The push notification content and metadata
     * @return DeliveryResult containing the outcome of the send operation
     */
    DeliveryResult send(List<Recipient> recipients, PushPayload payload);

}
