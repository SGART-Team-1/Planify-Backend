package iso.e02.planify.services;

import iso.e02.planify.entities.Administrator;
// imports de iso.e02.planify
import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.AdministratorRepository;
import iso.e02.planify.repositories.UsersRepository;

import java.util.HashMap;
// imports de java
import java.util.Map;
import java.util.Optional;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

// imports de google-authenticator
import com.warrenstrange.googleauth.GoogleAuthenticator;

// imports de javax.servlet
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servicio que gestiona las operaciones de inicio de sesión de los usuarios.
 * Proporciona funcionalidades para autenticar usuarios y generar tokens de
 * sesión.
 */
@Service
public class UserServiceLogin {

	@Autowired
	private UsersRepository userRepository; // Inyección del repositorio de usuarios

	@Autowired
	private AdministratorRepository adminRepository; // Inyección del repositorio de administradores

	@Autowired
	private ValidateUserService validateUserService; // Inyección del servicio de validación de usuarios

	@Autowired
	private ManageUsersService managerUserService; // Inyección del servicio de gestión de usuarios

	@Autowired
	private JWTService jwtService; // Inyección del servicio de tokens JWT

	@Autowired
	private IpBlockService ipBlockService; // Inyección del servicio de bloqueo de IPs

	private GoogleAuthenticator gAuth = new GoogleAuthenticator();
	private static final String ERROR = "Credenciales inválidas o usuario bloqueado/inactivo";

/**
 * Intenta autenticar al usuario con las credenciales proporcionadas.
 * 
 * @param email       El correo electrónico del usuario que intenta iniciar sesión.
 * @param password    La contraseña del usuario que intenta iniciar sesión.
 * @param httpRequest La solicitud HTTP que contiene la dirección IP del cliente.
 * @return Un mapa que contiene el token de sesión, el rol del usuario y su ID.
 */
public Map<String, Object> login(String email, String password, HttpServletRequest httpRequest) {
    String clientIp = httpRequest.getRemoteAddr(); // Obtiene la dirección IP del cliente

    // Buscar usuario en repositorios
    AppUser user = findUserByEmail(email).orElseThrow(() -> handleFailedLogin(clientIp));

    // Validar credenciales
    if (!validatePassword(password, user)) {
        handleInvalidPassword(user, clientIp);
    }

    // Verificar estado del usuario
    if (user instanceof CommonUser) {
        validateCommonUserState((CommonUser) user, clientIp);
    }

    // Restablecer intentos fallidos y preparar respuesta
    ipBlockService.resetAttempts(clientIp); 
    return createLoginResponse(user);
}

/**
 * Busca un usuario por correo electrónico en los repositorios disponibles.
 */
private Optional<AppUser> findUserByEmail(String email) {
    return userRepository.findByEmail(email)
            .or(() -> adminRepository.findByEmail(email));
}

/**
 * Maneja un intento fallido de inicio de sesión y lanza una excepción.
 */
private RuntimeException handleFailedLogin(String clientIp) {
    ipBlockService.registerFailedAttempt(clientIp);
    return new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR);
}

/**
 * Valida si la contraseña proporcionada coincide con la almacenada.
 */
private boolean validatePassword(String password, AppUser user) {
    return validateUserService.doHashesMatch(password, user.getCredentials().getPassword());
}

/**
 * Maneja un caso de contraseña inválida.
 */
private void handleInvalidPassword(AppUser user, String clientIp) {
    if (user instanceof CommonUser) {
        handleCommonUserInvalidPassword((CommonUser) user);
    }
    ipBlockService.registerFailedAttempt(clientIp);
    throw new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR);
}

/**
 * Maneja el incremento de intentos fallidos para un usuario común.
 */
private void handleCommonUserInvalidPassword(CommonUser commonUser) {
    if (!commonUser.isBlocked()) {
        commonUser.setLoginAttempts(commonUser.getLoginAttempts() + 1);
    }

    if (commonUser.getLoginAttempts() >= 5) {
        managerUserService.blockUser(commonUser.getId());
        commonUser.setLoginAttempts(0);
    }

    userRepository.save(commonUser);
}

/**
 * Valida el estado de un usuario común.
 */
private void validateCommonUserState(CommonUser commonUser, String clientIp) {
    if (!commonUser.isActivated() || commonUser.isBlocked()) {
        ipBlockService.registerFailedAttempt(clientIp);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, ERROR);
    }
    commonUser.setLoginAttempts(0);
    userRepository.save(commonUser);
}

/**
 * Crea la respuesta del inicio de sesión con la información del usuario.
 */
private Map<String, Object> createLoginResponse(AppUser user) {
    Map<String, Object> userObject = new HashMap<>();
    userObject.put("id", user.getId());
    userObject.put("role", user.getType());

    if (user instanceof Administrator) {
        setHttpHeader(user);
    }

    return userObject;
}

	/**
	 * Verifica la autenticación de dos factores del usuario y, si es correcta,
	 * devuelve un mapa el rol del usuario y su ID y el JWT en la cabezera
	 * de autorización.
	 * 
	 * @param userId          El ID del usuario que intenta iniciar sesión.
	 * @param verificationCode El código de verificación de la autenticación de dos factores.
	 * VerificationCode es el codigo que se introduce de la app de google authenticator
	 * @return Un mapa que contiene el el rol del usuario y su ID. Y el JWT en la cabezera
	 * de autorización.
	 */
	public void secondFactorValidated(Long userId, int verificationCode) {
		Optional<AppUser> optAppUser = userRepository.findById(userId) // Buscar al usuario en ambos repositorios
				.or(() -> adminRepository.findById(userId));
		String secretKey = optAppUser
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
				.getCredentials().getSecretKey(); // Obtener la clave secreta del usuario
		boolean isCodeValid = gAuth.authorize(secretKey, verificationCode); // Verificar el código de autenticación
		if (!isCodeValid) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Código de verificación inválido");
		}
		AppUser user = optAppUser.get();
		setHttpHeader(user);
	}

	@SuppressWarnings("null")
	public void setHttpHeader(AppUser user) {
		String jwt = jwtService.generarJWT(user);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		response.setHeader( "Authorization", "Bearer " + jwt);
		response.setHeader("Access-Control-Expose-Headers","Authorization");
	}
}