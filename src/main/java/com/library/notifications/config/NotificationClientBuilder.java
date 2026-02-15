package com.library.notifications.config;

import com.library.notifications.adapter.email.mailgun.MailgunEmailAdapter;
import com.library.notifications.adapter.email.sendgrid.SendGridEmailAdapter;
import com.library.notifications.adapter.sms.twilio.TwilioSmsAdapter;
import com.library.notifications.api.NotificationService;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.port.SmsProviderPort;
import com.library.notifications.core.sender.ChannelSender;
import com.library.notifications.core.sender.EmailChannelSender;
import com.library.notifications.core.sender.SmsChannelSender;
import com.library.notifications.core.usecase.SendNotificationUseCase;
import com.library.notifications.core.validation.EmailValidator;
import com.library.notifications.core.validation.SmsValidator;

import java.util.ArrayList;
import java.util.List;

import static com.library.notifications.api.exception.error.ValidationErrorCode.NO_SENDERS_CONFIGURED;

public class NotificationClientBuilder {

    private EmailProviderPort emailProviderPort;
    private EmailValidator emailValidator = new EmailValidator();
    private SmsProviderPort smsProviderPort;
    private SmsValidator smsValidator = new SmsValidator();

    public static NotificationClientBuilder builder() {
        return new NotificationClientBuilder();
    }

    // Choose email provider
    public NotificationClientBuilder useSendGrid(String apiKey) {
        this.emailProviderPort = new SendGridEmailAdapter(new SendGridEmailAdapter.SendGridConfig(apiKey));
        return this;
    }

    public NotificationClientBuilder useMailgun(String apiKey, String domain) {
        this.emailProviderPort = new MailgunEmailAdapter(new MailgunEmailAdapter.MailgunConfig(apiKey, domain));
        return this;
    }

    // Choose SMS provider
    public NotificationClientBuilder useTwilio(String accountSid, String authToken) {
        this.smsProviderPort = new TwilioSmsAdapter(new TwilioSmsAdapter.TwilioConfig(accountSid, authToken));
        return this;
    }

    public NotificationClientBuilder emailValidator(EmailValidator validator) {
        this.emailValidator = validator;
        return this;
    }

    public NotificationClientBuilder smsValidator(SmsValidator validator) {
        this.smsValidator = validator;
        return this;
    }

    public NotificationService build() {
        List<ChannelSender> senders = new ArrayList<>();

        if (emailProviderPort != null) {
            senders.add(new EmailChannelSender(emailProviderPort, emailValidator));
        }
        if (smsProviderPort != null) {
            senders.add(new SmsChannelSender(smsProviderPort, smsValidator));
        }

        // Future: senders.add(new ExampleChannelSender(...))

        if (senders.isEmpty()) {
            throw new ValidationException(NO_SENDERS_CONFIGURED);
        }

        return new SendNotificationUseCase(senders);
    }
}
