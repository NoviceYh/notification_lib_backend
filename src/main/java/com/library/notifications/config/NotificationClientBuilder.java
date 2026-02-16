package com.library.notifications.config;

import com.library.notifications.adapter.email.MailgunEmailAdapter;
import com.library.notifications.adapter.email.SendGridEmailAdapter;
import com.library.notifications.adapter.push.FirebasePushAdapter;
import com.library.notifications.adapter.sms.TwilioSmsAdapter;
import com.library.notifications.api.NotificationService;
import com.library.notifications.api.exception.ValidationException;
import com.library.notifications.core.port.EmailProviderPort;
import com.library.notifications.core.port.PushProviderPort;
import com.library.notifications.core.port.SmsProviderPort;
import com.library.notifications.core.sender.ChannelSender;
import com.library.notifications.core.sender.EmailChannelSender;
import com.library.notifications.core.sender.PushChannelSender;
import com.library.notifications.core.sender.SmsChannelSender;
import com.library.notifications.core.usecase.SendNotificationUseCase;
import com.library.notifications.core.validation.EmailValidator;
import com.library.notifications.core.validation.PushValidator;
import com.library.notifications.core.validation.SmsValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static com.library.notifications.api.exception.error.ValidationErrorCode.NO_SENDERS_CONFIGURED;
import static java.util.concurrent.ForkJoinPool.commonPool;

public class NotificationClientBuilder {

    private EmailProviderPort emailProviderPort;
    private EmailValidator emailValidator = new EmailValidator();
    private SmsProviderPort smsProviderPort;
    private SmsValidator smsValidator = new SmsValidator();
    private PushProviderPort pushProviderPort;
    private PushValidator pushValidator = new PushValidator();

    private Executor asyncExecutor;

    public static NotificationClientBuilder builder() {
        return new NotificationClientBuilder();
    }

    // Choose email provider
    public NotificationClientBuilder useSendGrid(String apiKey, String fromEmail) {
        this.emailProviderPort = new SendGridEmailAdapter(new SendGridEmailAdapter.SendGridConfig(apiKey, fromEmail));
        return this;
    }

    public NotificationClientBuilder useMailgun(String apiKey, String domain, String fromEmail) {
        this.emailProviderPort = new MailgunEmailAdapter(new MailgunEmailAdapter.MailgunConfig(apiKey, domain, fromEmail));
        return this;
    }

    // Choose SMS provider
    public NotificationClientBuilder useTwilio(String accountSid, String authToken, String fromPhoneNumber) {
        this.smsProviderPort = new TwilioSmsAdapter(new TwilioSmsAdapter.TwilioConfig(accountSid, authToken, fromPhoneNumber));
        return this;
    }

    // Choose Push provider
    public NotificationClientBuilder useFirebasePush(String projectId, String serviceAccountCredentials) {
        this.pushProviderPort =
                new FirebasePushAdapter(new FirebasePushAdapter.FirebaseConfig(projectId, serviceAccountCredentials));
        return this;
    }

    // === VALIDATORS ===
    public NotificationClientBuilder emailValidator(EmailValidator validator) {
        this.emailValidator = validator;
        return this;
    }
    public NotificationClientBuilder smsValidator(SmsValidator validator) {
        this.smsValidator = validator;
        return this;
    }
    public NotificationClientBuilder pushValidator(PushValidator validator) {
        this.pushValidator = validator;
        return this;
    }

    /**
     * Configures the executor used for asynchronous notification delivery.
     * If not set, ForkJoinPool.commonPool() is used.
     */
    public NotificationClientBuilder asyncExecutor(Executor executor) {
        this.asyncExecutor = executor;
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
        if (pushProviderPort != null) {
            senders.add(new PushChannelSender(pushProviderPort, pushValidator));
        }

        // Future: senders.add(new ExampleChannelSender(...))

        if (senders.isEmpty()) {
            throw new ValidationException(NO_SENDERS_CONFIGURED);
        }

        Executor executor = asyncExecutor != null
                ? asyncExecutor
                : commonPool();

        return new SendNotificationUseCase(senders, executor);
    }
}
