com.tuempresa.notifications
├── api
│    ├── NotificationService
│    ├── model
│    │     ├── NotificationRequest (o Notification)
│    │     ├── EmailPayload
│    │     ├── SmsPayload
│    │     ├── PushPayload
│    │     ├── Recipient
│    │     ├── DeliveryResult
│    │     └── Channel (enum)
│    └── exception
│          ├── ValidationException
│          └── DeliveryException
│
├── core
│    ├── usecase
│    │     └── SendNotificationUseCase (implementa NotificationService)
│    ├── sender
│    │     ├── ChannelSender
│    │     ├── EmailChannelSender
│    │     ├── SmsChannelSender
│    │     └── PushChannelSender
│    ├── port
│    │     ├── EmailProviderPort
│    │     ├── SmsProviderPort
│    │     └── PushProviderPort
│    └── validation
│          ├── EmailValidator
│          └── ...
│
├── adapter
│    ├── email
│    │     ├── sendgrid
│    │     │     ├── SendGridEmailAdapter” (implements EmailProviderPort)
│    │     │     └── dto (SendGridEmailRequest/Response)
│    │     └── mailgun
│    ├── sms
│    └── push
│
└── config
├── NotificationClientBuilder (o NotificationServiceFactory)
└── NotificationConfig (opcional)
