package com.library.notifications.api;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    DeliveryResult send(NotificationRequest request);

    default CompletableFuture<DeliveryResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request));
    }

    default CompletableFuture<List<DeliveryResult>> sendBatchAsync(
            List<NotificationRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<CompletableFuture<DeliveryResult>> futures = requests.stream()
                .map(this::sendAsync)
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        futures.stream()
                                .map(CompletableFuture::join)
                                .toList()
                );
    }

}
