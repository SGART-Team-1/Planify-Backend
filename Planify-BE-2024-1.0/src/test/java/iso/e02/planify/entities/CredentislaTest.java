package iso.e02.planify.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    private Credentials credentials;

    @BeforeEach
    void setUp() {
        credentials = new Credentials();
    }

    @Test
    void testId() {
        Long id = 1L;
        credentials.setId(id);
        assertEquals(id, credentials.getId());
    }

    @Test
    void testPassword() {
        String password = "myEncryptedPassword123"; // Simulamos una contraseña encriptada
        credentials.setPassword(password);
        assertEquals(password, credentials.getPassword());
    }
}
