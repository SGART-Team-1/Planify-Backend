package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.entities.Administrator;
import iso.e02.planify.requests.CreateAdminRequest;
import iso.e02.planify.services.ManageAdministratorService;
import iso.e02.planify.services.ValidateUserService;

// imports de java
import java.util.List;
import java.util.Map;
import java.util.Optional;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para gestionar las operaciones relacionadas con administradores.
 * 
 * Permite realizar operaciones CRUD como crear, editar, eliminar, listar
 * e inspeccionar administradores.
 * Cada método se encarga de una operación específica y utiliza el servicio
 * de administradores y el servicio de validación de usuarios para gestionar
 * la lógica de negocio.
 */
@RestController
@RequestMapping("/api/administrators") // Ruta base para los puntos de acceso de administradores
@CrossOrigin("*")
public class ManageAdministratorController {

    @Autowired
    private ManageAdministratorService administratorService; // Injección del servicio de administradores
                                                             
    @Autowired
    private ValidateUserService validateUserService; // Injección del servicio de validación de usuarios
    
    /**
     * Obtiene una lista de administradores con información básica para mostrar.
     * 
     * @return una respuesta HTTP con una lista de mapas que contienen información
     *         básica de los administradores.
     */
    @GetMapping("/showAdmins")
    public ResponseEntity<List<Map<String, Object>>> getAdminsToShow() {
        return ResponseEntity.ok(administratorService.getAdminsToShow()); // Llama al servicio de administradores para obtener la lista de administradores
    }

    /**
     * Obtiene un administrador específico por su ID.
     * 
     * @param id el ID del administrador a inspeccionar.
     * @return una respuesta HTTP con los detalles del administrador o un error 404
     *         si no se encuentra.
     */
    @GetMapping("/{id}/inspect")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable Long id) {
        Optional<Administrator> administrator = administratorService.getAdministratorById(id); // Llama al servicio de administradores para obtener el administrador
        return administrator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo administrador en el sistema si la información introducida es
     * válida.
     * 
     * @param adminInfo objeto con la información del administrador a crear.
     */
    @PostMapping("/create")
    public void createAdministrator(@RequestBody CreateAdminRequest adminInfo) {
        this.validateUserService.validateAdminInfo(adminInfo); // Validación de la información del administrador
        Administrator administrator = this.validateUserService.toAdmin(adminInfo); // Conversión de la información a un objeto de administrador
        administratorService.createAdministrator(administrator); // Llamada al servicio de administradores para crear el administrador
    }

    /**
     * Edita la información de un administrador existente.
     * 
     * @param id                   el ID del administrador a editar.
     * @param updatedAdministrator objeto con los datos actualizados del
     *                             administrador.
     */
    @PutMapping("/{id}/edit")
    public void editAdministrator(@PathVariable Long id, @RequestBody Administrator updatedAdministrator) {
        administratorService.editAdministrator(id, updatedAdministrator); // Llamada al servicio de administradores para editar el administrador
    }

    /**
     * Elimina un administrador específico por su ID.
     * 
     * @param id el ID del administrador a eliminar.
     */
    @DeleteMapping("/{id}")
    public void deleteAdministrator(@PathVariable Long id) {
        administratorService.deleteAdministrator(id); // Llamada al servicio de administradores para eliminar el administrador
    }
}