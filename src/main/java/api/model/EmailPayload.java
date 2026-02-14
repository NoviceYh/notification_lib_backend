package api.model;

public record EmailPayload(
        String subject,
        String body
) implements NotificationPayload {
}
