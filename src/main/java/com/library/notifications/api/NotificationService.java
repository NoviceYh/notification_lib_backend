package com.library.notifications.api;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;

public interface NotificationService {

    DeliveryResult send(NotificationRequest request);

}
