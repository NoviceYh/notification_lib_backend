package com.library.notifications.api;

import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    /**
     * Sends a notification based on the provided request.
     *
     * @param request the notification request containing all necessary information
     * @return a DeliveryResult indicating the outcome of the send operation
     */
    DeliveryResult send(NotificationRequest request);

    /**
     * Sends a notification asynchronously based on the provided request.
     *
     * @param request the notification request containing all necessary information
     * @return a CompletableFuture that will complete with a DeliveryResult indicating the outcome of the send operation
     */
    default CompletableFuture<DeliveryResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request));
    }

    /**
     * Sends a batch of notifications based on the provided list of requests.
     *
     * @param requests a list of notification requests to be sent
     * @return a list of DeliveryResult objects corresponding to each request
     */
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
