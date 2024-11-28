package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.requests.LoginRequest;
import iso.e02.planify.services.UserServiceLogin;

// imports de java
import java.util.Map;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// imports de jakarta
import jakarta.servlet.http.HttpServletRequest;


/**
 * Controlador que gestiona las solicitudes de inicio de sesión de los usuarios.
 * Se utiliza un segundo factor de autenticación para verificar la identidad del usuario.
 */
@RestController
@RequestMapping("users") // Ruta base para los puntos de acceso de inicio de sesión
@CrossOrigin("*")
public class UserControllerLogin {

	@Autowired
	private UserServiceLogin userService; // Inyección del servicio de usuario

	/**
	 * Maneja la solicitud de inicio de sesión de un usuario, controlando bloqueos de
	 * cuentas por intentos incorrectos y por IP.
	 * 
	 * @param request Contiene las credenciales del usuario (email y contraseña).
	 * @param httpRequest La solicitud HTTP que contiene la dirección IP del cliente.
	 * @return Un mapa que contiene el rol del usuario y el token en la cabecera
	 * de autorización de la respuesta.
	 */
	@PutMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
		Map<String, Object> userObject = this.userService.login(request.getEmail(), request.getPassword(), httpRequest); // Llama al servicio de inicio de sesión
		return ResponseEntity.ok(userObject); 
	}
	
	/**
	 * Verifica el segundo factor de autenticación del usuario.
	 * 
	 * @param userId El ID del usuario que se está autenticando.
	 * @param verificationCode El código de verificación que el usuario ha introducido.
	 * @return Un mapa que contiene el rol del usuario y el token en la cabecera
	 * de autorización de la respuesta.
	 */
	@PutMapping("/{userId}/second-factor-verify/{verificationCode}")
    public void secondFactorVerify(@PathVariable Long userId, @PathVariable int verificationCode) {
		this.userService.secondFactorValidated(userId, verificationCode); // Llama al servicio de verificación del segundo factor
    }
}