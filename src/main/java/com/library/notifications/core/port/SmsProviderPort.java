package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.Recipient;
import com.library.notifications.api.model.SmsPayload;

import java.util.List;

public interface SmsProviderPort {

    /**
     * Sends an SMS to the specified recipients with the given payload.
     *
     * @param recipients List of recipients to send the SMS to
     * @param payload    The SMS content and metadata
     * @return DeliveryResult containing the outcome of the send operation
     */
    DeliveryResult send(List<Recipient> recipients, SmsPayload payload);
}
