package com.library.notifications.adapter.email;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.Provider;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;
import static org.junit.jupiter.api.Assertions.*;

class SendGridEmailAdapterTest {

    @Test
    void constructor_shouldThrow_whenConfigIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new SendGridEmailAdapter(null));

        assertEquals("config is required", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenApiKeyIsNullOrEmpty(String apiKey) {
        SendGridEmailAdapter.SendGridConfig config =
                new SendGridEmailAdapter.SendGridConfig(apiKey, "example.com");
        SendGridEmailAdapter adapter = new SendGridEmailAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @Test
    void send_shouldReturnSuccessResult_whenApiKeyIsValid() {
        SendGridEmailAdapter.SendGridConfig config =
                new SendGridEmailAdapter.SendGridConfig("valid-api-key", "example.com");
        SendGridEmailAdapter adapter = new SendGridEmailAdapter(config);

        var result = adapter.send(null, null);

        assertNotNull(result);
        assertEquals(Provider.SENDGRID.name(), result.provider());
        assertTrue(result.providerMessageId().startsWith("sg-"));
        assertNotNull(result.timestamp());
    }

    @Test
    void send_shouldReturnFailedResult_whenRecipientsExceedLimit() {
        SendGridEmailAdapter.SendGridConfig config =
                new SendGridEmailAdapter.SendGridConfig("valid-api-key", "example.com");
        SendGridEmailAdapter adapter = new SendGridEmailAdapter(config);

        var recipients = new ArrayList<Recipient>();
        for (int i = 0; i < 51; i++) {
            recipients.add(new Recipient("user" + i + "@example.com"));
        }

        var result = adapter.send(recipients, null);

        assertNotNull(result);
        assertEquals(Provider.SENDGRID.name(), result.provider());
        assertNull(result.providerMessageId());
        assertNotNull(result.timestamp());
        assertEquals("rate limited by provider", result.errorMessage());
    }

}