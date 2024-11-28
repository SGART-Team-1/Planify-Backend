package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.entities.AppUser;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.RegisterRequest;
import iso.e02.planify.services.ManageUsersService;
import iso.e02.planify.services.MeetingService;
import iso.e02.planify.services.ValidateUserService;

// imports de java
import java.util.List;
import java.util.Map;
import java.util.Optional;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador REST para gestionar usuarios en la aplicación.
 * 
 * Permite realizar operaciones CRUD como obtener, editar, bloquear,
 * desbloquear, activar, eliminar usuarios. Tambien proporciona puntos
 * de acceso para validar el JWT de un usuario y comprobar si tiene reuniones.
 */
@RestController
@RequestMapping("/api/users") // Ruta base para los puntos de acceso de usuarios
@CrossOrigin("*")
public class ManageUsersController {

    @Autowired
    private ManageUsersService userService; // Injección del servicio de usuarios

    @Autowired
    private ValidateUserService validateUserService; // Injección del servicio de validación de usuarios

    @Autowired
	private UsersRepository appUserRepository; // Injección del repositorio de usuarios

    @Autowired
    private MeetingService meetingService; // Inyección del servicio de reuniones

    /**
     * Obtiene una lista de usuarios con datos específicos para mostrar.
     *
     * @return ResponseEntity que contiene una lista de usuarios en formato de mapa
     *         de datos.
     */
    @GetMapping("/showUsers")
    public ResponseEntity<List<Map<String, Object>>> getUserToShow() {
        return ResponseEntity.ok(userService.getUserToShow()); // Llama al servicio de usuarios para obtener la lista de usuarios
    }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * Como éste método lo usan tanto administradores como usuarios, se ha añadido
     * una validación de acceso para comprobar si el usuario que hace la petición
     * está intentando acceder a su propia información.
     * 
     * @param userId ID del usuario a obtener.
     * @return ResponseEntity que contiene el usuario en caso de encontrarlo.
     */
    @GetMapping("/{userId}/inspect")
    public ResponseEntity<CommonUser> getUserById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long userId) {
        this.validateUserService.validateAccess(authorizationHeader, userId); // Validación de acceso, debido al uso por parte de ambos roles
        Optional<CommonUser> user = userService.getUserById(userId); // Llama al servicio de usuarios para obtener el usuario
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Bloquea a un usuario por su ID.
     *
     * @param userId ID del usuario a bloquear.
     */
    @PutMapping("/{userId}/block")
    public void blockUser(@PathVariable Long userId) {
            userService.blockUser(userId); //Llama al servicio de usuarios para bloquear al usuario
    }

    /**
     * Desbloquea a un usuario por su ID.
     *
     * @param userId ID del usuario a desbloquear.
     */
    @PutMapping("/{userId}/unblock")
    public void unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId); //Llama al servicio de usuarios para desbloquear al usuario
    }

    /**
     * Activa a un usuario por su ID.
     *
     * @param userId ID del usuario a activar.
     */
    @PutMapping("/{userId}/activate")
    public void activateUser(@PathVariable Long userId) {
        userService.activateUser(userId); //Llama al servicio de usuarios para activar al usuario
        
    }

    /**
     * Edita la información de un usuario por su ID.
     *
     * @param userId      ID del usuario a editar.
     * @param userInfo Objeto RegisterRequest con la nueva información del usuario.
     */
    @PutMapping("/{userId}/edit")
    public void editUser(@PathVariable Long userId, @RequestBody RegisterRequest userInfo) {
            this.validateUserService.validateUserInfoEdit(userInfo); // Validación de la información a guardar de la edición
		    CommonUser updatedUser = this.validateUserService.toCommonUser(userInfo); // Conversión de la información a un objeto CommonUser
            userService.editUser(userId, updatedUser); // Llama al servicio de usuarios para editar al usuario  
    }

    /**
     * Elimina a un usuario por su ID.
     *
     * @param userId ID del usuario a eliminar.
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId); // Llama al servicio de usuarios para eliminar al usuario
    }

    /**
     * Comprueba si un usuario tiene reuniones abiertas.
     *
     * @param userId ID del usuario a comprobar.
     * @return ResponseEntity que contiene un booleano que indica si el usuario tiene reuniones abiertas.
     */
    @GetMapping("/{userId}/hasOpenedMeetings")
    public ResponseEntity<Boolean> hasOpenedMeetings(@PathVariable Long userId) {
        validateUserService.validateCommonUserExist(userId); // Validación de que el usuario exista
        return ResponseEntity.ok(this.meetingService.hasOpenedMeetings(userId)); // Llama al servicio de reuniones para comprobar si el usuario tiene reuniones abiertas
    }

    /**
     * Valida el JWT de un usuario y devuelve sus datos.
     * 
     * @param authorizationHeader la cabecera de autorización con el JWT del usuario.
     * @return el usuario asociado al JWT.
     */
    @GetMapping("/validateJWT")
    public ResponseEntity<AppUser> validateJWT(@RequestHeader("Authorization") String authorizationHeader) {
        String email = validateUserService.validateJWT(authorizationHeader); // Validación del JWT
        Optional<AppUser> optAppUser = appUserRepository.findByEmail(email); // Búsqueda del usuario en el repositorio
        // Si no se encuentra el usuario, se lanza una excepción
        if (optAppUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usted se está intentando colar en el sistema");
        }
        return ResponseEntity.ok(optAppUser.get());
    }
}