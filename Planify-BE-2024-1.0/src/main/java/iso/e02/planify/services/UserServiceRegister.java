package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.UsersRepository;

// imports de java
import java.util.Map;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// imports de google-authenticator
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

/**
 * Servicio que gestiona el registro de nuevos usuarios.
 * Proporciona funcionalidades para guardar un nuevo usuario en la base de
 * datos.
 */
@Service
public class UserServiceRegister {

	@Autowired
	private UsersRepository userRepository; // Inyección del repositorio de usuarios.

	private GoogleAuthenticator gAuth = new GoogleAuthenticator();

	/**
	 * Registra un nuevo usuario en el sistema.
	 *
	 * Este método intenta guardar un objeto {@link CommonUser} en la base de datos.
	 * Si el usuario ya existe, se lanza una excepción de estado.
	 *
	 * @param user El usuario que se desea registrar.
	 */
	public void register(CommonUser user) {
		try {
			GoogleAuthenticatorKey key = gAuth.createCredentials();
			String secretKey = key.getKey();
			user.getCredentials().setSecretKey(secretKey);
			this.userRepository.save(user);
		} catch (DataIntegrityViolationException e) { // Si el usuario ya existe, se lanza una excepción de estado.
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya está registrado.");
		}
	}

	/**
	 * Obtiene la URL del código QR para la autenticación en dos pasos.
	 *
	 * Este método genera la URL del código QR que se utilizará para la autenticación
	 * en dos pasos.
	 *
	 * @param user El usuario para el que se desea obtener la URL del código QR.
	 * @return Un mapa con la URL del código QR.
	 */
	public Map<String, Object> getQrCodeUrl(CommonUser user) {
		String secretKey = user.getCredentials().getSecretKey();
		String issuer = "Planify";
		// Genera la URL del código QR
		return Map.of(
				"qr_code", GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, user.getEmail(),
						new GoogleAuthenticatorKey.Builder(secretKey).build()));
	}
}