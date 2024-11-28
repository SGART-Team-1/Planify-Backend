package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.UserServiceRegister;
import iso.e02.planify.services.ValidateUserService;

// imports de java
import java.util.Map;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que gestiona las solicitudes de registro de usuarios.
 * Proporciona un endpoint para registrar nuevos usuarios en el sistema.
 */
@RestController
@RequestMapping("users") // ruta base para los puntos de acceso de usuarios
@CrossOrigin("*")
public class UserControllerRegister {

	@Autowired
	private UserServiceRegister userService; // Inyección del servicio de usuarios

	@Autowired
	private ValidateUserService validateUserService; // Inyección del servicio de validación de usuarios

	/**
	 * Registra un nuevo usuario.
	 * 
	 * Este método recibe una petición HTTPs que contiene la información del nuevo
	 * usuario, valida los datos y realiza el registro.
	 *
	 * @param userInfo La información del usuario a registrar, encapsulada en un
	 *                 objeto {@link RegisterRequest}.
	 */
	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerUser(@RequestBody RegisterRequest userInfo) {
		this.validateUserService.validateUserInfo(userInfo); // Validación de los datos del usuario
		CommonUser user = this.validateUserService.toCommonUser(userInfo); // Conversión de la información a un objeto CommonUser
		user.setActivated(false); // El usuario se registra desactivado
		this.userService.register(user); // Llamada al servicio de usuarios para registrar el usuario
		return ResponseEntity.ok(this.userService.getQrCodeUrl(user)); // Devolución de la URL del código QR
	}
}