package iso.e02.planify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import iso.e02.planify.config.SystemPropertiesConfig;

/**
 * Clase principal de la aplicación que inicia el contexto de Spring Boot.
 * Extiende {@link SpringBootServletInitializer} para permitir la implementación de la aplicación en un entorno de servidor de aplicaciones.
 */
@SpringBootApplication
@ServletComponentScan
public class App extends SpringBootServletInitializer {

    /**
     * Método principal de la aplicación, invocado al ejecutar la aplicación desde la línea de comandos.
     * Carga las propiedades del sistema antes de iniciar el contexto de Spring Boot.
     * 
     * @param args los argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        // Cargar las propiedades del sistema antes de iniciar la aplicación.
        SystemPropertiesConfig.loadSystemProperties();

        // Iniciar la aplicación con Spring Boot.
        SpringApplication.run(App.class, args);
    }

    /**
     * Método sobrescrito para configurar la aplicación cuando se implementa en un entorno de servidor.
     * Permite construir el contexto de la aplicación y especificar la clase de configuración.
     * 
     * @param builder el constructor de la aplicación de Spring.
     * @return el objeto {@link SpringApplicationBuilder} con la configuración de la aplicación.
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // Configurar la aplicación indicando que la clase de configuración es 'App'.
        return builder.sources(App.class);
    }
}
