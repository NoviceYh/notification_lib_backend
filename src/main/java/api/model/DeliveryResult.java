package api.model;

import java.time.Instant;

public record DeliveryResult(
        Status status,
        String provider,
        String providerMessageId,
        Instant timestamp,
        String errorMessage
) {
    public enum Status { SUCCESS, FAILED }

    public static DeliveryResult success(String provider, String providerMessageId, Instant timestamp) {
        return new DeliveryResult(Status.SUCCESS, provider, providerMessageId, timestamp, null);
    }

    public static DeliveryResult failed(String provider, Instant timestamp, String errorMessage) {
        return new DeliveryResult(Status.FAILED, provider, null, timestamp, errorMessage);
    }
}
