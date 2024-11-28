package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangePasswordRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange: Define test values for email and password
        String testEmail = "user@example.com";
        String testPassword = "newSecurePassword123";

        // Act: Create an instance of ChangePasswordRequest using the test values
        ChangePasswordRequest request = new ChangePasswordRequest(testEmail, testPassword);
        
        assertEquals(testEmail, request.getEmail());

        // Test password field
        assertEquals(testPassword, request.getPassword());
    }
}
