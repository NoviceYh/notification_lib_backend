package com.library.notifications.adapter.push;

import com.library.notifications.api.exception.DeliveryException;
import com.library.notifications.api.model.Provider;
import com.library.notifications.api.model.Recipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;

import static com.library.notifications.api.exception.error.DeliveryErrorCode.PROVIDER_AUTH_ERROR;
import static org.junit.jupiter.api.Assertions.*;

class FirebasePushAdapterTest {

    @Test
    void constructor_shouldThrow_whenConfigIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> new FirebasePushAdapter(null));

        assertEquals("config is required", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenProjectIdIsNullOrEmpty(String projectId) {
        FirebasePushAdapter.FirebaseConfig config =
                new FirebasePushAdapter.FirebaseConfig(projectId, "valid");
        FirebasePushAdapter adapter = new FirebasePushAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void send_shouldThrow_whenServiceAccountCredentialsIsNullOrEmpty(String serviceAccountCredentials) {
        FirebasePushAdapter.FirebaseConfig config =
                new FirebasePushAdapter.FirebaseConfig("projectId", serviceAccountCredentials);
        FirebasePushAdapter adapter = new FirebasePushAdapter(config);

        DeliveryException ex = assertThrows(DeliveryException.class,
                () -> adapter.send(null, null));

        assertEquals(PROVIDER_AUTH_ERROR.description(), ex.getMessage());
    }

    @Test
    void send_shouldReturnSuccessResult_whenCredentialsAreValid() {
        FirebasePushAdapter.FirebaseConfig config =
                new FirebasePushAdapter.FirebaseConfig("valid", "valid");
        FirebasePushAdapter adapter = new FirebasePushAdapter(config);

        var result = adapter.send(null, null);

        assertNotNull(result);
        assertEquals(Provider.FIREBASE.name(), result.provider());
        assertTrue(result.providerMessageId().startsWith("fb-"));
        assertNotNull(result.timestamp());
    }

    @Test
    void send_shouldReturnFailedResult_whenRecipientsExpired() {
        FirebasePushAdapter.FirebaseConfig config =
                new FirebasePushAdapter.FirebaseConfig("valid", "valid");
        FirebasePushAdapter adapter = new FirebasePushAdapter(config);

        var recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient("expired"));

        var result = adapter.send(recipients, null);

        assertNotNull(result);
        assertEquals(Provider.FIREBASE.name(), result.provider());
        assertNull(result.providerMessageId());
        assertNotNull(result.timestamp());
        assertEquals("registration token not registered", result.errorMessage());
    }

}