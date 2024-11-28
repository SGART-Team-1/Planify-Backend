package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.Administrator;
import iso.e02.planify.repositories.AdministratorRepository;

// imports de java
import java.util.List;
import java.util.Map;
import java.util.Optional;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// imports de jakarta
import jakarta.transaction.Transactional;

/**
 * Servicio para gestionar las operaciones relacionadas con los administradores.
 * Proporciona métodos para crear, editar, eliminar y obtener información de los
 * administradores.
 */
@Service
public class ManageAdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository; // Inyección del repositorio de administradores.

    /**
     * Obtiene una lista de todos los administradores registrados en el sistema.
     *
     * @return una lista de objetos Administrator.
     */
    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    /**
     * Obtiene una lista de administradores con solo los datos necesarios para
     * mostrar.
     * Incluye nombre, apellido, correo electrónico y ID.
     *
     * @return una lista de mapas con la información básica de cada administrador.
     */
    public List<Map<String, Object>> getAdminsToShow() {
        return administratorRepository.getAdminsToShow();
    }

    /**
     * Busca y obtiene un administrador específico por su ID.
     *
     * @param id el ID del administrador a buscar.
     * @return un objeto Optional<Administrator> que contiene el administrador si se
     *         encuentra, o vacío si no.
     */
    public Optional<Administrator> getAdministratorById(Long id) {
        return administratorRepository.findById(id);
    }

    /**
     * Crea y guarda un nuevo administrador en el sistema.
     * 
     * @param administrator el objeto Administrator que contiene la
     *                      información del nuevo administrador.
     * @return el administrador creado.
     */
    @Transactional
    public Administrator createAdministrator(Administrator administrator) {
        try {
            return this.administratorRepository.save(administrator);
        } catch (DataIntegrityViolationException e) { // Si el correo ya está registrado en la base de datos se lanza una excepción.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya está registrado.");
        } 
    }

    /**
     * Edita la información de un administrador existente.
     *
     * @param id                   el ID del administrador a editar.
     * @param updatedAdministrator el objeto Administrator con los datos
     *                             actualizados.
     * @throws ResponseStatusException si el administrador no es encontrado o si
     *                                 ocurre un error en la actualización.
     */
    @Transactional
    public void editAdministrator(Long id, Administrator updatedAdministrator) {
        Optional<Administrator> adminOptional = administratorRepository.findById(id);
        if (adminOptional.isPresent()) { // Validar que el administrador existe
            Administrator existingAdmin = adminOptional.get();
            // Actualizar los atributos heredados de AppUser
            existingAdmin.setName(updatedAdministrator.getName());
            existingAdmin.setSurnames(updatedAdministrator.getSurnames());
            // Si la foto es null no se introduce en la base de datos ni como 0x
            if (updatedAdministrator.getPhoto() == null || updatedAdministrator.getPhoto().length == 0) {
                existingAdmin.setPhoto(null);
            } else {
                existingAdmin.setPhoto(updatedAdministrator.getPhoto());
            }
            existingAdmin.setCentre(updatedAdministrator.getCentre());
            existingAdmin.setInternal(updatedAdministrator.isInternal());
            administratorRepository.save(existingAdmin); // Guardar los cambios
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador no encontrado"); 
        }
    }

    /**
     * Elimina un administrador específico del sistema por su ID.
     *
     * @param id el ID del administrador a eliminar.
     * @throws ResponseStatusException si el administrador no se encuentra.
     */
    @Transactional
    public void deleteAdministrator(Long id) {
        if (administratorRepository.existsById(id)) { // Comprobar que el administrador existe
            administratorRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Administrador no encontrado");
        }
    }
}