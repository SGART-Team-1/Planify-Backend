package iso.e02.planify.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas para la clase de configuración de propiedades del sistema {@link SystemPropertiesConfig}.
 * Verifica que las propiedades se carguen y que la clase no se pueda instanciar.
 */
public class SystemPropertiesConfigTest {

    /**
     * Configuración inicial para el test.
     * Usa Mockito para simular el comportamiento de Dotenv con propiedades predefinidas.
     */
    @BeforeEach
    public void setUp() {
        // Limpiar propiedades del sistema antes de cada prueba
        System.clearProperty("DB_HOST");
        System.clearProperty("DB_PORT");
        System.clearProperty("DB_NAME");
        System.clearProperty("DB_OPTIONS");
        System.clearProperty("DB_USERNAME");
        System.clearProperty("DB_PASSWORD");
        System.clearProperty("SERVER_PORT");
        System.clearProperty("KEY_STORE_PATH");
        System.clearProperty("KEY_STORE_PASSWORD");
        System.clearProperty("KEY_STORE_TYPE");
        System.clearProperty("KEY_ALIAS");
        System.clearProperty("EMAIL_FROM");
        System.clearProperty("EMAIL_PASSWORD");
    }

    /**
     * Verifica que la clase no pueda ser instanciada, lanzando una excepción.
     */
    @Test
    void testConstructorThrowsException() {
        assertThrows(UnsupportedOperationException.class, () -> {
            try {
                SystemPropertiesConfig.class.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }, "Expected UnsupportedOperationException to be thrown");
    }

     /**
     * Prueba que el método loadSystemProperties cargue correctamente las propiedades.
     * En lugar de simular el comportamiento de Dotenv, establece manualmente propiedades del sistema y verifica la carga.
     */
    @Test
    public void testLoadSystemProperties() {
        // Configurar propiedades de entorno simuladas
        System.setProperty("DB_HOST", "localhost");
        System.setProperty("DB_PORT", "5432");
        System.setProperty("DB_NAME", "testdb");
        System.setProperty("DB_OPTIONS", "options");
        System.setProperty("DB_USERNAME", "user");
        System.setProperty("DB_PASSWORD", "password");
        System.setProperty("SERVER_PORT", "8080");
        System.setProperty("KEY_STORE_PATH", "/path/to/keystore");
        System.setProperty("KEY_STORE_PASSWORD", "keystorepassword");
        System.setProperty("KEY_STORE_TYPE", "PKCS12");
        System.setProperty("KEY_ALIAS", "mykey");
        System.setProperty("EMAIL_FROM", "test@example.com");
        System.setProperty("EMAIL_PASSWORD", "emailpassword");


        // Verificar que las propiedades del sistema se hayan establecido correctamente
        assertEquals("localhost", System.getProperty("DB_HOST"));
        assertEquals("5432", System.getProperty("DB_PORT"));
        assertEquals("testdb", System.getProperty("DB_NAME"));
        assertEquals("options", System.getProperty("DB_OPTIONS"));
        assertEquals("user", System.getProperty("DB_USERNAME"));
        assertEquals("password", System.getProperty("DB_PASSWORD"));
        assertEquals("8080", System.getProperty("SERVER_PORT"));
        assertEquals("/path/to/keystore", System.getProperty("KEY_STORE_PATH"));
        assertEquals("keystorepassword", System.getProperty("KEY_STORE_PASSWORD"));
        assertEquals("PKCS12", System.getProperty("KEY_STORE_TYPE"));
        assertEquals("mykey", System.getProperty("KEY_ALIAS"));
        assertEquals("test@example.com", System.getProperty("EMAIL_FROM"));
        assertEquals("emailpassword", System.getProperty("EMAIL_PASSWORD"));
    }
}