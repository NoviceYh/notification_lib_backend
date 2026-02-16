package com.library.notifications.core.usecase;

import com.library.notifications.api.NotificationService;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.api.model.Channel;
import com.library.notifications.api.model.DeliveryResult;
import com.library.notifications.api.model.NotificationRequest;
import com.library.notifications.core.sender.ChannelSender;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static com.library.notifications.api.exception.error.ValidationErrorCode.*;

public class SendNotificationUseCase implements NotificationService {

    private final Map<Channel, ChannelSender> senderByChannel;

    private final Executor executor;

    public SendNotificationUseCase(List<ChannelSender> senders) {
        this(senders, ForkJoinPool.commonPool());
    }

    public SendNotificationUseCase(List<ChannelSender> senders, Executor executor) {
        this.executor = Objects.requireNonNull(executor, "executor cannot be null");
        if (senders == null) {
            throw new ValidationException(SENDERS_NULL);
        }

        EnumMap<Channel, ChannelSender> map = new EnumMap<>(Channel.class);
        for (ChannelSender sender : senders) {
            Channel channel = Objects.requireNonNull(sender.channel(), "sender.channel() cannot be null");
            if (map.containsKey(channel)) {
                throw new ValidationException(DUPLICATE_SENDER_FOR_CHANNEL, channel.name());
            }
            map.put(channel, sender);
        }
        this.senderByChannel = Map.copyOf(map);
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        if (request == null) throw new ValidationException(REQUEST_NULL);
        if (request.channel() == null) throw new ValidationException(EMPTY_CHANNEL);

        ChannelSender sender = senderByChannel.get(request.channel());
        if (sender == null) {
            throw new ValidationException(SENDER_NOT_CONFIGURED, request.channel().name());
        }
        return sender.send(request);
    }

    @Override
    public CompletableFuture<DeliveryResult> sendAsync(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

}
