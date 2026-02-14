package adapter.email.sendgrid.dto;

public record SendGridEmailResponse(
        boolean success,
        String providerMessageId,
        String errorMessage) {
}
