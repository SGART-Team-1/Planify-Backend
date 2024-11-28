package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.AppUser;
import iso.e02.planify.repositories.UsersRepository;

// imports de java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// imports de javax.mail
import javax.mail.MessagingException;

/**
 * Servicio para gestionar la recuperación de contraseñas de los usuarios.
 * Incluye la funcionalidad para enviar correos electrónicos de recuperación
 * y cambiar contraseñas.
 */
@Service
public class RecoveryPasswordService {

    Map<String, TokenPasswordChange> tokens = new HashMap<>(); // contiene el token y el email del usuario

    @Autowired
    private UsersRepository userRepository; // Inyección del repositorio de usuarios

    @Autowired
    private ValidateUserService validateUserService; // Inyección del servicio de validación de usuarios

    /**
     * Envía un correo electrónico de recuperación de contraseña al usuario con
     * el correo proporcionado, incluyendo un token único.
     *
     * @param email Dirección de correo electrónico a la que se enviará el
     *              correo de recuperación.
     * @return Mensaje confirmando que el correo ha sido enviado.
     */
    public String sendEmail(String email) {
        /**
         * Enviar email de recuperación de contraseña
         * enviamos un token al email que sera recibido en el cambio de contraseña
         * con esto nos aseguramos que el usuario que esta cambiando la contraseña es el
         * dueño de la cuenta
         */
        Optional<AppUser> optUser = userRepository.findByEmail(email);
        if (!optUser.isEmpty()) { // si existe el usuario o admin envia el email
            String idToken = UUID.randomUUID().toString();
            TokenPasswordChange token = new TokenPasswordChange(idToken, email);
            this.tokens.put(idToken, token);
            EmailSMTP smtp = new EmailSMTP();
            try {
                smtp.sendEmail(email, idToken); // enviamos el email
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return "Email enviado, revise su bandeja de entrada y SPAM";
    }

    /**
     * Cambia la contraseña del usuario asociado al correo electrónico proporcionado
     * utilizando el token de recuperación.
     *
     * @param email    Dirección de correo electrónico del usuario.
     * @param token    Token de recuperación enviado al correo electrónico.
     * @param password Nueva contraseña del usuario.
     * @return Mensaje confirmando que la contraseña ha sido cambiada con éxito.
     */
    public String changePassword(String email, String token, String password) {
        TokenPasswordChange tokenEmail = this.tokens.get(token); // token que se envio al email
        if (tokenEmail == null || !tokenEmail.getEmail().equals(email) || tokenEmail.isCaducado()) { // validar si el token no existe o no coincide con el email o esta caducado
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token inválido o caducado");
        }
        Optional<AppUser> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) { // validar si el usuario no existe
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no existe");
        }
        if (this.validateUserService.isPasswordSecure(password)) { // Comprobamos si la nueva contraseña cumple la política de contraseñas
            AppUser user = optUser.get();
            user.getCredentials().setPassword(this.validateUserService.hashPassword(password)); // Cambiamos la contraseña
            userRepository.save(user); // Guardamos los cambios
            tokens.remove(token); // Eliminamos el token
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La contraseña no es segura");
        }
        return "Contraseña cambiada con exito";
    }
}