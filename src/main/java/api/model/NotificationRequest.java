package api.model;

import java.util.List;

public record NotificationRequest(
        Channel channel,
        List<Recipient> recipients,
        NotificationPayload payload
) {
}
