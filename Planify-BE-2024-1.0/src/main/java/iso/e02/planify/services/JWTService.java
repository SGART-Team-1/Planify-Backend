package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;

// imports de java
import java.util.Date;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// imports de auth0
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


/*
 * Servicio que se encarga de la generación y validación de tokens JWT.
 * Tambien permite obtener el usuario a partir de un token JWT.
 */
@Service
public class JWTService {

    @Autowired
    private ManageUsersService manageUsersService; // Inyección del servicio ManageUsersService

    @Autowired
    private ValidateUserService validateUserService; // Inyección del servicio ValidateUserService

    /*
     * Método que obtiene el usuario a partir de un token JWT.
     * 
     * @param authorizationHeader el token JWT que se va a validar. 
     * @return el usuario común que corresponde al token JWT.
     */
    public CommonUser getCommonUserFromJWT(String authorizationHeader) {
        String userEmail = this.validateUserService.validateJWT(authorizationHeader); // Llama al servicio de validación de usuarios para validar el token JWT
        return this.manageUsersService.getUserByEmail(userEmail); // Llama al servicio de gestión de usuarios para obtener el usuario a partir del email
    }

    /*
     * Método que genera un token JWT para un usuario.
     * 
     * @param user el usuario para el que se va a generar el token JWT.
     * @return el token JWT generado.
     */
    public String generarJWT(AppUser user) {
       return JWT.create()
            .withSubject(user.getEmail())            // Asigna el email como sujeto del token
            .withIssuedAt(new Date())                // Fecha de emisión
            .withExpiresAt(new Date(System.currentTimeMillis() + 604800000 )) // Expiración en 7 días
            .sign(Algorithm.HMAC256(System.getProperty("JWT_SECRET")));    // Firma con HMAC256 y la clave secreta
    }
}