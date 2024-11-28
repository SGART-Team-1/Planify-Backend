package iso.e02.planify.security;

// imports de iso.e02.planify
import iso.e02.planify.entities.Administrator;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.services.ValidateUserService;

// imports de java
import java.util.Arrays;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

// imports de jakarta
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor de Spring que se encarga de interceptar las peticiones HTTP y validar el token JWT.
 * 
 * Esta clase implementa la interfaz HandlerInterceptor de Spring, que define métodos que se ejecutan
 * antes y después de que se maneje una petición HTTP.
 * 
 * El método preHandle se ejecuta antes de que se maneje la petición HTTP. En este método se extrae el 
 * token JWT del encabezado Authorization, se valida y se comprueba si el usuario tiene permisos para acceder a la ruta solicitada.
 */
@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private ValidateUserService validateUserService; // Inyección del servicio de validación de JWT

    @Autowired
    private UsersRepository usersRepository; // Inyección del repositorio de usuarios


    AntPathMatcher pathMatcher = new AntPathMatcher();

    public JWTInterceptor(ValidateUserService validateUserService) { // Constructor de la clase
        this.validateUserService = validateUserService;
    }

    /**
     * Método que se ejecuta antes de manejar la petición HTTP.
     * 
     * @param request  La petición HTTP.
     * @param response La respuesta HTTP.
     * @param handler  El controlador que manejará la petición.
     * @return true si la petición debe continuar, false si debe detenerse.
     * @throws Exception Si ocurre un error al manejar la petición.
     */
    @SuppressWarnings("null")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) { // Skip `OPTIONS` requests
            return true;
        }
        String jwt = request.getHeader("Authorization"); // Extraer el token JWT del encabezado Authorization
        if (jwt == null || !jwt.startsWith("Bearer ")) { // Comprobar si el token JWT es nulo o no comienza con "Bearer "
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid");
            return false;
        }
        jwt = jwt.replace("Bearer ", ""); // Quitar el prefijo "Bearer " del token JWT
        try {
            String email = validateUserService.validateJWT(jwt); // Validar el token JWT y extraer el email del usuario
            String role = "CommonUser";
            if (usersRepository.findByEmail(email).get() instanceof Administrator) { // Comprobar si el usuario es un administrador
                role = "Administrator";
            }

            // Validar el acceso a los end points del back dependiendo del rol del usuario
            String requestPath = request.getRequestURI();
            String[] adminRoutes = { // Rutas de administrador
                "/absences/*/delete",  
                "/absences/create",     
                "/workSchedule/addWorkSchedule",
                "/api/administrators/**", 
                "/api/users/showUsers",
                "/api/users/*/block",
                "/api/users/*/unblock",
                "/api/users/*/activate",
                "/api/users/registerUser",
                "/absences/*/list", 
                "/api/users/*/hasOpenedMeetings",
                "/api/users/*",
                "/absences/checkMeetingOverlap" 
            };
            
            String[] commonUserRoutes = { // Rutas de usuario común
                "/meetings/**",          
                "/absences/list"       
            };
            
            String[] sharedRoutes = {
                "/api/users/*/inspect",
                "/api/users/validateJWT" // Rutas accesibles por ambos roles
            };

            // Validar rutas compartidas primero
            if (Arrays.stream(sharedRoutes).anyMatch(route -> pathMatcher.match(route, requestPath))) {
                return true; // Permitir acceso a rutas compartidas
            }
            else if (Arrays.stream(adminRoutes).anyMatch(route -> pathMatcher.match(route, requestPath)) &&  "CommonUser".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                "Acceso denegado, función solo disponible para administradores");
                return false;

            } else if (Arrays.stream(commonUserRoutes).anyMatch(route -> pathMatcher.match(route, requestPath)) && "Administrator".equals(role)) {// funcionalidades de usuario común
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acceso denegado, función solo disponible para usuarios comunes");
                return false;
            } 
            else{
                return true; // Permitir acceso a rutas permitidas
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token"); // Enviar un error 401 si el token JWT es inválido o ha expirado
            return false;
        }
    }
}