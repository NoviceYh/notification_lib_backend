# Notifications Library

A Java library for sending notifications through multiple channels (Email, SMS, Push) using a clean, extensible and provider-agnostic architecture.

The library is designed following **Hexagonal Architecture** principles, focusing on clear separation between domain logic and external integrations.


---
## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Example Usage](#example-usage)
- [Quick Start with Docker](#quick-start-with-docker)
- [Configuration](#configuration)
- [Asynchronous Notifications](#asynchronous-notifications)
- [Providers Supported](#providers-supported)
- [API Reference](#api-reference-core)
- [Extensibility](#extensibility)
- [Future Improvements](#future-improvements)
- [Design Principles](#design-principles-solid)

---

## Features

- Multiple notification channels: **Email**, **SMS**, **Push**
- Multiple providers per channel (e.g. SendGrid / Mailgun for Email)
- Type-safe, code-based configuration (no YAML or properties files)
- Domain-specific validation and error modeling
- Synchronous and asynchronous notification delivery
- Batch sending support
- Easily extensible architecture for new channels and providers

---

## Installation

### Maven

```xml
<dependency>
  <groupId>com.library</groupId>
  <artifactId>notifications-lib</artifactId>
  <version>1.0.0</version>
</dependency>
```
---

## Example Usage

```java
import com.library.notifications.*;

NotificationService service = NotificationClientBuilder.builder()
    .useSendGrid("SG_API_KEY", "no-reply@example.com")
    .useTwilio("TWILIO_SID", "TWILIO_TOKEN", "+14155552671")
    .build();

NotificationRequest request = new NotificationRequest(
    Channel.EMAIL,
    List.of(new Recipient("user@example.com")),
    new EmailPayload("Welcome", "Hello from Notifications Library")
);

DeliveryResult result = service.send(request);
```
---
## Quick Start with Docker

This project is a Java library (no runnable server).

The Docker image is provided to run the build and test suite in a reproducible environment.

**Build**
```
docker build -t notifications-lib .
```

**Run tests**
```
docker run --rm notifications-lib
```

---
## Configuration

All configuration is done via Java code, keeping it explicit, type-safe and provider-aware.

Each provider requires different credentials according to its official documentation.
The builder exposes provider-specific configuration methods to reflect those requirements.

### Email Providers

**SendGrid**

```builder.useSendGrid("SG_API_KEY", "no-reply@example.com");```

**Mailgun**

```builder.useMailgun("MAILGUN_API_KEY", "example.com", "no-reply@example.com");```


_Email supports multiple providers. The provider is selected explicitly through the builder._

### SMS Providers

**Twilio**
```
builder.useTwilio(
"TWILIO_ACCOUNT_SID",
"TWILIO_AUTH_TOKEN",
"+14155552671"
);
```

_SMS recipients are validated using the E.164 international format, ensuring provider-agnostic phone number validation._

_Message length is validated using the standard 160 character limit to fail fast, without coupling to provider-specific segmentation rules._

### Push Providers

**Firebase Cloud Messaging**
```
builder.useFirebasePush(
"firebase-project-id",
"service-account-json"
);
```


_Push providers are modeled as adapters.
The core library only handles validation and orchestration, keeping provider-specific concerns isolated._

---
### Asynchronous Notifications

The library supports non-blocking notification delivery using **CompletableFuture.**

Asynchronous methods are implemented on top of the synchronous API to avoid code duplication and preserve existing behavior.

**Single notification (async)**
```
service.sendAsync(request)
.thenAccept(result -> { /* handle success */ })
.exceptionally(ex -> { /* handle error */ return null; });
```

**Batch sending (async)**
```
List<NotificationRequest> requests = List.of(request1, request2);

service.sendBatchAsync(requests)
.thenAccept(results -> { /* handle results */ });
```
**Custom Executor**
```
NotificationService service = NotificationClientBuilder.builder()
.useSendGrid("key", "no-reply@example.com")
.useTwilio("sid", "token", "+14155552671")
.asyncExecutor(Executors.newFixedThreadPool(4))
.build();
```

_If no executor is configured, ForkJoinPool.commonPool() is used by default._

---
## Providers Supported

| Channel | Providers |
|--------|-----------|
| Email  | SendGrid, Mailgun |
| SMS    | Twilio |
| Push   | Firebase Cloud Messaging |

---

## API Reference (Core)
### NotificationService
```
DeliveryResult send(NotificationRequest request);
CompletableFuture<DeliveryResult> sendAsync(NotificationRequest request);
CompletableFuture<List<DeliveryResult>> sendBatchAsync(List<NotificationRequest> requests);
```

### NotificationRequest

Represents a notification to be sent through a specific channel.

### DeliveryResult

Represents the outcome of a delivery attempt, including provider name, message ID and timestamp.

---
### Validation & Error Handling

Validation errors are represented using domain-specific error codes (e.g. EV-0004 – invalid email format).

HTTP-related concerns such as status codes are intentionally excluded from the library, since it is transport-agnostic and may be used in non-HTTP contexts.

Consumers are free to map these errors to HTTP responses or other error representations as needed.

---
### Provider-Specific DTOs

Provider adapters are currently implemented as simulated integrations.

For this reason, provider-specific request and response DTOs (such as SendGrid request/response models) are intentionally omitted from the core implementation.

In a real-world integration, each adapter would define its own request/response models to map domain data to the provider API and vice versa.

This design was deliberately kept out of scope for this challenge in order to focus on architecture, extensibility, and testability rather than HTTP client details.

---
### Retries

Automatic retry mechanisms are not implemented in this version.

The architecture explicitly distinguishes between validation errors and provider delivery errors, allowing retries to be added in the future for transient failures (e.g. provider timeouts or temporary unavailability) without impacting the core domain logic.

---
### Security Considerations

- API keys, tokens and credentials should never be hardcoded.

- Credentials should be injected from secure sources (environment variables, secret managers).

- The library does not log sensitive information such as API keys or tokens.

---
### Testing Notes

Mockito currently relies on Byte Buddy self-attachment for inline mocking.
Future versions of the JDK may restrict this behavior, so configuring the Java agent explicitly in the build is recommended for long-term compatibility.

Coverage report: 
run ```mvn clean test``` and open ```target/site/jacoco/index.html.```

---
### Extensibility

Once the Email channel was implemented, adding new channels like SMS or Push became straightforward, since the routing, validation, and delivery contracts were already defined.

**Each new channel only requires its own:**

- Payload

- Validator

- Provider port

- Channel sender

_No changes to existing use cases are required._

---
## Future Improvements

The library was designed with extensibility in mind. Possible future improvements include:

- Retry and resilience mechanisms for transient failures
- Provider fallback strategies per channel
- Metrics and observability hooks
- Rate limiting and throttling
- Scheduled or delayed notification delivery
- Support for carbon copy (CC) and blind carbon copy (BCC) recipients in email notifications.
- Ability to send HTML-based emails and integrate template engines for dynamic, reusable email templates.


These features can be introduced without impacting the existing core use cases.

---
## Design Principles (SOLID)

The library was designed following the SOLID principles to ensure clarity, extensibility and maintainability.

### Single Responsibility Principle (SRP)
Each class has a single, well-defined responsibility:
- `EmailValidator`, `SmsValidator`, `PushValidator` handle validation rules specific to each channel.
- `EmailChannelSender`, `SmsChannelSender`, `PushChannelSender` orchestrate validation and delegate delivery to the corresponding provider.
- `SendNotificationUseCase` is responsible only for routing notifications to the appropriate channel sender.
- Provider adapters (e.g. `SendGridEmailAdapter`, `TwilioSmsAdapter`, `FirebasePushAdapter`) encapsulate provider-specific integration logic.
- `NotificationClientBuilder` acts as the composition root, responsible only for wiring and configuration.

---

### Open/Closed Principle (OCP)
The system is open for extension but closed for modification:
- New channels can be added by introducing new payloads, validators, provider ports and channel senders without modifying existing use cases.
- New providers can be added by implementing the corresponding provider port interface and exposing a new configuration method in the builder.

---

### Liskov Substitution Principle (LSP)
All implementations respect their abstraction contracts:
- Any implementation of `EmailProviderPort`, `SmsProviderPort` or `PushProviderPort` can be substituted without affecting channel senders.
- All `ChannelSender` implementations are interchangeable within the notification routing logic.

---

### Interface Segregation Principle (ISP)
Interfaces are small and purpose-specific:
- Provider ports are segregated by channel (`EmailProviderPort`, `SmsProviderPort`, `PushProviderPort`) instead of using a single generic provider interface.
- `ChannelSender` exposes only the minimal behavior required to send notifications.
- The public API (`NotificationService`) exposes a focused set of methods for sending notifications.

---

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions, not concrete implementations:
- Channel senders depend on provider port interfaces rather than concrete provider implementations.
- The core use case depends on `ChannelSender` abstractions instead of concrete senders.
- Concrete adapters are instantiated and wired at the edge of the system via `NotificationClientBuilder`.

---

### Architectural Patterns
- **Hexagonal Architecture (Ports and Adapters):** Core logic is isolated from external providers through well-defined ports.
- **Strategy Pattern:** Each notification channel is implemented as a strategy (`ChannelSender`) selected at runtime.
- **Builder Pattern / Composition Root:** The builder centralizes configuration and dependency wiring in code.
