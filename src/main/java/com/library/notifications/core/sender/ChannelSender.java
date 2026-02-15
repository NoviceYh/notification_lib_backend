package com.library.notifications.core.sender;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;

public interface ChannelSender {

    /**
     * @return the channel this sender is responsible for (e.g., EMAIL, SMS, PUSH)
     */
    Channel channel();

    /**
     * Send a notification request through the appropriate channel.
     * The implementation should handle validation and delivery logic specific to the channel.
     *
     * @throws ValidationException if the request is invalid for this channel (e.g., missing email address for EMAIL channel)
     * @throws DeliveryException if there is an error during the delivery process (e.g., provider API failure)
     */
    DeliveryResult send(NotificationRequest request);
}
