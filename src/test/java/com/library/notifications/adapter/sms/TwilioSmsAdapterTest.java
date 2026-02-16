package com.library.notifications.adapter.sms;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.Provider;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;
import static org.junit.jupiter.api.Assertions.*;

class TwilioSmsAdapterTest {

    @Test
    void constructor_shouldThrow_whenConfigIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new TwilioSmsAdapter(null));

        assertEquals("config is required", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenAccountSidIsNullOrEmpty(String accountSid) {
        TwilioSmsAdapter.TwilioConfig config =
                new TwilioSmsAdapter.TwilioConfig(accountSid, "authToken", "+1234567890");
        TwilioSmsAdapter adapter = new TwilioSmsAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenAuthTokenIsNullOrEmpty(String authToken) {
        TwilioSmsAdapter.TwilioConfig config =
                new TwilioSmsAdapter.TwilioConfig("accountSid", authToken, "+1234567890");
        TwilioSmsAdapter adapter = new TwilioSmsAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @Test
    void send_shouldReturnSuccessResult_whenCredentialsAreValid() {
        TwilioSmsAdapter.TwilioConfig config =
                new TwilioSmsAdapter.TwilioConfig("valid", "valid", "+1234567890");
        TwilioSmsAdapter adapter = new TwilioSmsAdapter(config);

        var result = adapter.send(null, null);

        assertNotNull(result);
        assertEquals(Provider.TWILIO.name(), result.provider());
        assertTrue(result.providerMessageId().startsWith("tw-"));
        assertNotNull(result.timestamp());
    }

    @Test
    void send_shouldReturnFailedResult_whenDestinationNotReachable() {
        TwilioSmsAdapter.TwilioConfig config =
                new TwilioSmsAdapter.TwilioConfig("valid", "valid", "+1234567890");
        TwilioSmsAdapter adapter = new TwilioSmsAdapter(config);

        var recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient("+1234567000"));

        var result = adapter.send(recipients, null);

        assertNotNull(result);
        assertEquals(Provider.TWILIO.name(), result.provider());
        assertNull(result.providerMessageId());
        assertNotNull(result.timestamp());
        assertEquals("destination not reachable", result.errorMessage());
    }

}