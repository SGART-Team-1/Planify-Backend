package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.entities.Absence;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.requests.CreateAbsenceRequest;
import iso.e02.planify.services.AbsencesService;
import iso.e02.planify.services.ValidateAbsenceService;
import iso.e02.planify.services.JWTService;
import iso.e02.planify.services.ManageUsersService;

import java.util.ArrayList;
// imports de java
import java.util.List;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar las ausencias de los usuarios.
 * 
 * Permite realizar operaciones CRUD como crear, listar y eliminar
 * ausencias de los usuarios. Cada método se encarga de una operación específica
 * y utiliza el servicio de ausencias y validacion de ausencias para gestionar la lógica de negocio.
 */
@RestController
@RequestMapping("absences") //ruta base para los puntos de acceso de ausencias
@CrossOrigin("*")
public class AbsencesController {

	@Autowired
	private AbsencesService absencesService; // Injección del servicio de ausencias

	@Autowired
	private ValidateAbsenceService validateAbsenceService; // Servicio de validación de ausencias

	@Autowired
	private JWTService jwtService;

	@Autowired
	private ManageUsersService manageUsersService;

	/**
	 * Comprueba si una ausencia se superpone con reuniones.
	 * 
	 * Este método recibe una solicitud con los detalles de la ausencia y
	 * comprueba si se superpone con otras ausencias del usuario.
	 * 
	 * @param absence el objeto de solicitud que contiene los detalles de la ausencia.
	 * @return true si la ausencia se superpone con otras ausencias, false en caso contrario.
	 */
	@PutMapping("/checkMeetingOverlap")
	public ResponseEntity<Boolean> checkMeetingOverlap(@RequestBody CreateAbsenceRequest absenceInfo) {
		this.validateAbsenceService.validateAbsenceInfo(absenceInfo); //validar la información de la ausencia
		return ResponseEntity.ok(this.absencesService.checkMeetingOverlap(absenceInfo));
	}

	/**
	 * Crea una nueva ausencia para un usuario específico.
	 * 
	 * Este método recibe una solicitud con los detalles de la ausencia,
	 * como las fechas y horas de inicio y fin, si dura toddo el día , el 
	 * ID del usuario y el tipo de ausencia, y la pasa alservicio de ausencias
	 * para validarla y guardarla en la base de datos.
	 * 
	 * @param absenceInfo el objeto de solicitud que contiene los datos de la ausencia.
	 */
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestBody CreateAbsenceRequest absenceInfo) {
		this.validateAbsenceService.validateAbsenceInfo(absenceInfo); // validación de la información de la ausencia
		
		 List<String> messages = new ArrayList<>();
		if (absenceInfo.hasOverlapsMeeting()) { //si la ausencia se superpone con reuniones
			messages = this.manageUsersService.cancelOrRejectMeetingsAbsenceInfo(absenceInfo); //se gestionará en el servicio de ausencias
		}
		
		Absence absence = this.validateAbsenceService.toAbsence(absenceInfo); // conversión de la información a un objeto de ausencia
		this.absencesService.create(absence); // creación de la ausencia en la base de datos
		
		return ResponseEntity.ok(messages);
	}

	/**
	 * Lista todas las ausencias de un usuario específico.
	 * 
	 * Este método devuelve una lista de ausencias asociadas al ID de un usuario.
	 * Utiliza servicios para validar el acceso permitido y que el usuario exista
	 * y recupera las ausencias desde la base de datos y las devuelve en una respuesta HTTP.
	 * Es necesario validar el acceso ya que ambos roles de la aplicación pueden utilizar este método.
	 * 
	 * @param id el ID del usuario cuyas ausencias se desean listar.
	 * @return una respuesta HTTP con una lista de ausencias.
	 */
	@GetMapping("/{id}/list")
	public ResponseEntity<List<Absence>> list(@PathVariable Long id) {
		this.manageUsersService.userExists(id);
		return ResponseEntity.ok(this.absencesService.list(id));
	}

	/**
	 * Lista todas las ausencias asociadas al JWT de un usuario.
	 * 
	 * Este método devuelve una lista de ausencias asociadas a un usuario por
	 * medio del token de sesión JWT.
	 * 
	 * @param la cabezera de autorización con el JWT del usuario cuyas ausencias se desean listar.
	 * @return una respuesta HTTP con una lista de ausencias.
	 */
	@GetMapping("/list")
	public ResponseEntity<List<Absence>> listAll(@RequestHeader("Authorization") String authorizationHeader) {
		CommonUser user = jwtService.getCommonUserFromJWT(authorizationHeader); //obtener el usuario desde el JWT
		return ResponseEntity.ok(this.absencesService.list(user.getId())); //devolver la lista de ausencias
	}

	/**
	 * Elimina una ausencia específica de la base de datos.
	 * 
	 * Este método elimina una ausencia usando su ID y llama al servicio de
	 * ausencias
	 * para realizar la operación en la base de datos.
	 * 
	 * @param absenceId el ID de la ausencia a eliminar.
	 */
	@DeleteMapping("/{absenceId}/delete")
	public void delete(@PathVariable long absenceId) {
		this.validateAbsenceService.validateAbsenceExist(absenceId); //validar que la ausencia exista
		this.absencesService.delete(absenceId); //eliminar la ausencia
	}
}