package com.library.notifications.core.port;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.EmailPayload;
import com.library.notifications.api.model.Recipient;

import java.util.List;

public interface EmailProviderPort {

    /**
     * Sends an email to the specified recipients with the given payload.
     *
     * @param recipients List of recipients to send the email to
     * @param payload    The email content and metadata
     * @return DeliveryResult containing the outcome of the send operation
     */
    DeliveryResult send(List<Recipient> recipients, EmailPayload payload);
}
