package iso.e02.planify.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de prueba para la clase LoginRequest.
 */
public class LoginRequestTest {

    /**
     * Test para el constructor y los métodos getter de la clase LoginRequest.
     */
    @Test
    public void testLoginRequest() {
        // Crear un objeto LoginRequest con valores de prueba
        String testEmail = "test@example.com";
        String testPassword = "password123";
        
        // Crear el objeto LoginRequest
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        
        // Verificar que el correo electrónico y la contraseña se establecieron correctamente
        assertEquals(testEmail, loginRequest.getEmail(), "El correo electrónico no coincide.");
        assertEquals(testPassword, loginRequest.getPassword(), "La contraseña no coincide.");
    }
}
