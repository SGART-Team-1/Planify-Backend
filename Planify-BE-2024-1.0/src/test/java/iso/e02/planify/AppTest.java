package iso.e02.planify;

import iso.e02.planify.config.SystemPropertiesConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.builder.SpringApplicationBuilder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Clase de prueba para la aplicación principal {@link App}.
 * Verifica que el contexto de Spring Boot se cargue correctamente y
 * que se llame a los métodos de configuración inicial correctamente.
 */
@SpringBootTest
public class AppTest {

    /**
     * Método de configuración previo a todos los tests, carga propiedades del sistema.
     */
    @BeforeAll
    public static void setUp() {
        SystemPropertiesConfig.loadSystemProperties();
    }

    /**
     * Prueba que el contexto de la aplicación se cargue sin errores.
     */
    @Test
    public void contextLoads() {
        // Este método asegura que el contexto de la aplicación se carga correctamente.
    }

    /**
     * Prueba el método 'configure' de la clase {@link App}.
     * Verifica que el 'SpringApplicationBuilder' se configure correctamente con la clase App.
     */
    @Test
    public void testConfigure() {
        // Crear un mock de SpringApplicationBuilder
        SpringApplicationBuilder builder = mock(SpringApplicationBuilder.class);
        
        // Llamar al método configure y verificar que se utiliza la clase App como fuente
        App app = new App();
        app.configure(builder);
        
        // Verificar que el builder fue configurado con la clase App
        verify(builder).sources(App.class);
    }
}
