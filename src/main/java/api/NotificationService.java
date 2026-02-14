package api;

import api.model.DeliveryResult;
import api.model.NotificationRequest;

public interface NotificationService {

    DeliveryResult send(NotificationRequest request);

}
