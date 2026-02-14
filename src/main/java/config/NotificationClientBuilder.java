package config;

import adapter.email.mailgun.MailgunEmailAdapter;
import adapter.email.sendgrid.SendGridEmailAdapter;
import api.NotificationService;
import core.port.EmailProviderPort;
import core.sender.ChannelSender;
import core.sender.EmailChannelSender;
import core.usecase.SendNotificationUseCase;
import core.validation.EmailValidator;

import java.util.ArrayList;
import java.util.List;

public class NotificationClientBuilder {

    private EmailProviderPort emailProviderPort;
    private EmailValidator emailValidator = new EmailValidator(); // default

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
