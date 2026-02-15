package com.library.notifications.config;

import com.library.notifications.adapter.email.mailgun.MailgunEmailAdapter;
import com.library.notifications.adapter.email.sendgrid.SendGridEmailAdapter;
import com.library.notifications.api.NotificationService;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.sender.ChannelSender;
import com.library.notifications.core.sender.EmailChannelSender;
import com.library.notifications.core.usecase.SendNotificationUseCase;
import com.library.notifications.core.validation.EmailValidator;

import java.util.ArrayList;
import java.util.List;

public class NotificationClientBuilder {

    private EmailProviderPort emailProviderPort;
    private EmailValidator emailValidator = new EmailValidator();

    public static NotificationClientBuilder builder() {
        return new NotificationClientBuilder();
    }

    // Elegís provider en código:
    public NotificationClientBuilder useSendGrid(String apiKey) {
        this.emailProviderPort = new SendGridEmailAdapter(new SendGridEmailAdapter.SendGridConfig(apiKey));
        return this;
    }

    public NotificationClientBuilder useMailgun(String apiKey, String domain) {
        this.emailProviderPort = new MailgunEmailAdapter(new MailgunEmailAdapter.MailgunConfig(apiKey, domain));
        return this;
    }

    public NotificationClientBuilder emailValidator(EmailValidator validator) {
        this.emailValidator = validator;
        return this;
    }

    public NotificationService build() {
        List<ChannelSender> senders = new ArrayList<>();

        if (emailProviderPort != null) {
            senders.add(new EmailChannelSender(emailProviderPort, emailValidator));
        }

        // Después: senders.add(new SmsChannelSender(...))
        // Después: senders.add(new PushChannelSender(...))

        return new SendNotificationUseCase(senders);
    }
}
