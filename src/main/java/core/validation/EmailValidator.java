package core.validation;

import api.exception.ValidationException;
import api.model.EmailPayload;
import api.model.Recipient;
import core.sender.EmailChannelSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class EmailValidator {

    private static final Logger log = LoggerFactory.getLogger(EmailValidator.class);


    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public void validate(List<Recipient> recipients, EmailPayload payload) {
        validateRecipients(recipients);
        payloadValidation(payload);
    }

    private void validateRecipients(List<Recipient> recipients) {

        if (recipients == null || recipients.isEmpty()) {
            log.error("La lista de destinatarios no puede ser vacía");
            throw new ValidationException("La lista de destinatarios no puede ser vacía");
        }

        for (Recipient recipient : recipients) {
            String email = recipient.value();
            if (email == null || email.isBlank()) {
                log.error("Cada destinatario debe tener un email válido");
                throw new ValidationException("Cada destinatario debe tener un email válido");
            }
            if (email.contains("..")) {
                log.error("Email con puntos consecutivos: {}", email);
                throw new ValidationException("Email has consecutive dots: " + email);
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                log.error("Email con formato inválido: {}", email);
                throw new IllegalArgumentException("Invalid email: " + email);
            }
        }
    }

    private void payloadValidation(EmailPayload payload) {
        if (payload == null) {
            log.error("El payload no puede ser null");
            throw new ValidationException("El payload no puede ser null");
        }
        if (payload.subject() == null || payload.subject().isBlank()) {
            log.error("El asunto del email no puede ser vacío");
            throw new ValidationException("El asunto del email no puede ser vacío");
        }
        if (payload.body() == null || payload.body().isBlank()) {
            log.error("El cuerpo del email no puede ser vacío");
            throw new ValidationException("El cuerpo del email no puede ser vacío");
        }

    }

}
