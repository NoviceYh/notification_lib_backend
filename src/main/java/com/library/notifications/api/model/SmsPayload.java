package com.library.notifications.api.model;

public record SmsPayload(String message) implements NotificationPayload {
}