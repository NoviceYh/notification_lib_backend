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

public class SendNotificationUseCase implements NotificationService {

    private final Map<Channel, ChannelSender> senderByChannel;

    public SendNotificationUseCase(List<ChannelSender> senders) {
        Objects.requireNonNull(senders, "senders es obligatorio");

        EnumMap<Channel, ChannelSender> map = new EnumMap<>(Channel.class);
        for (ChannelSender sender : senders) {
            Channel channel = Objects.requireNonNull(sender.channel(), "sender.channel() no puede ser null");
            if (map.containsKey(channel)) {
                throw new IllegalArgumentException("Hay más de un ChannelSender registrado para " + channel);
            }
            map.put(channel, sender);
        }
        this.senderByChannel = Map.copyOf(map);
    }

    @Override
    public DeliveryResult send(NotificationRequest request) {
        if (request == null) throw new IllegalArgumentException("La notificación no puede ser null");
        if (request.channel() == null) throw new IllegalArgumentException("El channel es obligatorio");

        ChannelSender sender = senderByChannel.get(request.channel());
        if (sender == null) {
            throw new IllegalArgumentException("No hay sender configurado para channel " + request.channel());
        }
        return sender.send(request);
    }
}
