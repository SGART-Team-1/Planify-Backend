package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.services.RecoveryPasswordService;
import iso.e02.planify.requests.ChangePasswordRequest;

// imports de java
import java.util.Map;
import java.util.HashMap;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar las operaciones relacionadas con la recuperación
 * de contraseñas,
 * incluyendo el envío de correos electrónicos de recuperación y el cambio de
 * contraseñas.
 */
@RestController
@RequestMapping("users") // Ruta base para los puntos de acceso de recuperación de contraseñas
@CrossOrigin("*")
public class RecoveryController {

    @Autowired
    private RecoveryPasswordService recoveryServices; // Inyección del servicio de recuperación de contraseñas

    /**
     * Envía un correo electrónico de recuperación a la dirección proporcionada.
     *
     * @param email Dirección de correo electrónico a la que se enviará el correo de
     *              recuperación.
     * @return Un mapa que contiene el resultado del envío del correo.
     */
    @PostMapping("/sendRecoveyEmail")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestParam String email) {
        Map<String, String> resultado = new HashMap<>();
        resultado.put("resultado", this.recoveryServices.sendEmail(email)); // Llama al servicio de recuperación de contraseñas para enviar el correo
        return ResponseEntity.ok(resultado);
    }

    /**
     * Cambia la contraseña del usuario utilizando el token de recuperación
     * proporcionado.
     *
     * @param request Objeto que contiene la información necesaria para cambiar la
     *                contraseña,
     *                incluyendo el correo electrónico y la nueva contraseña.
     * @param token   Token de recuperación utilizado para validar la solicitud de
     *                cambio de contraseña.
     * @return Un mapa que contiene el resultado del cambio de contraseña.
     */
    @PutMapping("/changePassword")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody ChangePasswordRequest request, @RequestParam String token) {
        Map<String, String> resultado = new HashMap<>();

        resultado.put("resultado",
            this.recoveryServices.changePassword(request.getEmail(), token, request.getPassword())); // Llama al servicio de recuperación de contraseñas para cambiar la contraseña
        return ResponseEntity.ok(resultado);
    }
}