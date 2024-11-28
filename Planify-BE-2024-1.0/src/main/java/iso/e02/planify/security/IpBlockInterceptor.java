package iso.e02.planify.security;

// imports de iso.e02.planify
import iso.e02.planify.services.IpBlockService;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// imports de jakarta.servlet
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Interceptor para manejar bloqueos por IP en el endpoint de login.
 */
@Component
public class IpBlockInterceptor implements HandlerInterceptor {

    @Autowired
    private IpBlockService ipBlockService; // Inyección del servicio de bloqueo por IP

    @Autowired
    public IpBlockInterceptor(IpBlockService ipBlockService) { // Constructor de la clase
        this.ipBlockService = ipBlockService;
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
        String clientIp = request.getRemoteAddr(); // Obtener la IP del cliente
        if (ipBlockService.isIpBlocked(clientIp)) { // Verificar si la IP está bloqueada
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "IP bloqueada temporalmente");
            return false;
        }
        return true; // Continuar si la IP no está bloqueada
    }
}