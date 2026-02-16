package com.library.notifications.adapter.email;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;
import static org.junit.jupiter.api.Assertions.*;

class MailgunEmailAdapterTest {

    @Test
    void constructor_shouldThrow_whenConfigIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new MailgunEmailAdapter(null));

        assertEquals("config is required", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenApiKeyIsNullOrEmpty(String apiKey) {
        MailgunEmailAdapter.MailgunConfig config =
                new MailgunEmailAdapter.MailgunConfig(apiKey, "example.com", "");
        MailgunEmailAdapter adapter = new MailgunEmailAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @Test
    void send_shouldReturnSuccessResult_whenApiKeyIsValid() {
        MailgunEmailAdapter.MailgunConfig config =
                new MailgunEmailAdapter.MailgunConfig("valid-api-key", "example.com", "");
        MailgunEmailAdapter adapter = new MailgunEmailAdapter(config);

        var result = adapter.send(null, null);

        assertNotNull(result);
        assertEquals(Provider.MAILGUN.name(), result.provider());
        assertTrue(result.providerMessageId().startsWith("mg-"));
        assertNotNull(result.timestamp());
    }


}