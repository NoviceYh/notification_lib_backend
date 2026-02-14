package adapter.email.sendgrid.dto;

public record SendGridEmailRequest(
        String from,
        String to,
        String subject,
        String content) {
}
