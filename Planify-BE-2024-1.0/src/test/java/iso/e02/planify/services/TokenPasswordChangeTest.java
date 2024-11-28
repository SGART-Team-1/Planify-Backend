package iso.e02.planify.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenPasswordChangeTest {
    private TokenPasswordChange token;
    private String testId;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testId = "test-token-id";
        testEmail = "test@example.com";
        token = new TokenPasswordChange(testId, testEmail);
    }

    @Test
    void testConstructor() {
        assertEquals(testId, token.getId());
        assertEquals(testEmail, token.getEmail());
        assertNotNull(token.getHoraFin());
        assertFalse(token.isCaducado()); // Should not be expired immediately after creation
    }

    @Test
    void testIsCaducado_WithinDuration() throws InterruptedException {
        // Wait for a short time to ensure the token has not expired
        Thread.sleep(1000); // 1 second
        assertFalse(token.isCaducado(), "Token should not be expired within its duration");
    }

  

    @Test
    void testRefrescar() throws InterruptedException {
       
        token.refrescar(); // Refresh the token

        assertFalse(token.isCaducado(), "Token should not be expired after refreshing");
    }

    @Test
    void testSettersAndGetters() {
        token.setId("new-token-id");
        token.setEmail("new@example.com");
        token.setHoraFin(1234567890L);

        assertEquals("new-token-id", token.getId());
        assertEquals("new@example.com", token.getEmail());
        assertEquals(1234567890L, token.getHoraFin());
    }
}
