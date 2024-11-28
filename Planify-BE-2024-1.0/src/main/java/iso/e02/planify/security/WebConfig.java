package iso.e02.planify.security;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase de configuración de Spring que se encarga de configurar el CORS y los interceptores de las peticiones HTTP.
 * 
 * Esta clase implementa la interfaz WebMvcConfigurer de Spring, que define métodos para configurar el manejo de las peticiones HTTP.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JWTInterceptor jwtInterceptor; // Inyección del interceptor de JWT
    
    @Autowired
    private IpBlockInterceptor ipBlockInterceptor; // Inyección del interceptor de bloqueo por IP
    
    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")  // poner el dominio de la app de angular
                .allowedMethods("GET", "POST", "DELETE","PATCH", "PUT", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization");
    }
    
    @Autowired
    public WebConfig(JWTInterceptor jwtInterceptor, IpBlockInterceptor ipBlockInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.ipBlockInterceptor = ipBlockInterceptor;
    }

    @Override
    public void addInterceptors(@SuppressWarnings("null") InterceptorRegistry registry) { // Método para añadir los interceptores
        
        registry.addInterceptor(jwtInterceptor) // Añadir las rutas que deben ser interceptadas por el interceptor de JWT
            .addPathPatterns("/absences/**", "/users/**", "/api/**", "/workSchedule/**" , "/meetings/**") 
            .excludePathPatterns("/users/login", "/users/register", "/users/sendRecoveyEmail", "/users/changePassword" , "/users/*/second-factor-verify/*");
        registry.addInterceptor(ipBlockInterceptor) // Añadir las rutas que deben ser interceptadas por el interceptor de bloqueo por IP
            .addPathPatterns("/users/login"); 
    }
}