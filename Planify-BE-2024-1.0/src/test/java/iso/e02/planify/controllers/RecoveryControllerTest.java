package iso.e02.planify.controllers;

import iso.e02.planify.requests.ChangePasswordRequest;
import iso.e02.planify.services.RecoveryPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RecoveryControllerTest {

    @InjectMocks
    private RecoveryController recoveryController; // Controller to test

    @Mock
    private RecoveryPasswordService recoveryServices; // Mocked service

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testSendEmail() {
        // Given
        String email = "test@example.com";
        String expectedMessage = "Email sent"; // Expected message
        when(recoveryServices.sendEmail(email)).thenReturn(expectedMessage); // Mocking service

        // When
        ResponseEntity<Map<String, String>> response = recoveryController.sendEmail(email);
        Map<String, String> responseBody = response.getBody();
        // Then
        verify(recoveryServices, times(1)).sendEmail(email); // Verify service interaction
        assertNotNull(responseBody);
        assertEquals(expectedMessage, responseBody.get("resultado")); // Verify response
    }

    @Test
    void testChangePassword() {
       // Arrange: Define test values for email and password
       String testEmail = "user@example.com";
       String testPassword = "newSecurePassword123";

       // Act: Create an instance of ChangePasswordRequest using the test values
       ChangePasswordRequest request = new ChangePasswordRequest(testEmail, testPassword);
        String token = "validToken";
        String expectedMessage = "Password changed successfully"; // Expected message
        when(recoveryServices.changePassword(request.getEmail(), token, request.getPassword()))
                .thenReturn(expectedMessage); // Mocking service

        // When
        ResponseEntity<Map<String, String>> response = recoveryController.changePassword(request, token);
        Map<String, String> responseBody = response.getBody();
        // Then
        verify(recoveryServices, times(1)).changePassword(request.getEmail(), token, request.getPassword()); // Verify service interaction
        assertNotNull(responseBody);
        assertEquals(expectedMessage, responseBody.get("resultado")); // Verify response
    }
}