package iso.e02.planify.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Clase de configuración de propiedades del sistema.
 * Carga las propiedades del sistema desde un archivo .env y las establece como propiedades del sistema.
 * 
 * Esta es una clase de utilidad que no se puede instanciar.
 */
public class SystemPropertiesConfig {

    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidad.
     * Lanza una excepción si se intenta instanciar la clase.
     */
    private SystemPropertiesConfig() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    /**
     * Carga las propiedades del sistema desde el archivo .env utilizando la librería Dotenv.
     * Establece estas propiedades como propiedades del sistema, que pueden ser utilizadas en otras partes de la aplicación.
     */
    public static void loadSystemProperties() {
        // Cargar las variables del archivo .env
        Dotenv dotenv = Dotenv.load();

        // Configurar las propiedades del sistema
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_OPTIONS", dotenv.get("DB_OPTIONS"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("SERVER_PORT", dotenv.get("SERVER_PORT"));
        System.setProperty("KEY_STORE_PATH", dotenv.get("KEY_STORE_PATH"));
        System.setProperty("KEY_STORE_PASSWORD", dotenv.get("KEY_STORE_PASSWORD"));
        System.setProperty("KEY_STORE_TYPE", dotenv.get("KEY_STORE_TYPE"));
        System.setProperty("KEY_ALIAS", dotenv.get("KEY_ALIAS"));
        System.setProperty("EMAIL_FROM", dotenv.get("EMAIL_FROM"));
        System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
    }
}
