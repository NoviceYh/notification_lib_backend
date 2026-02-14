package com.library.notifications.api.model;

public record PushPayload(
        String title,
        String message
) implements NotificationPayload {
}
