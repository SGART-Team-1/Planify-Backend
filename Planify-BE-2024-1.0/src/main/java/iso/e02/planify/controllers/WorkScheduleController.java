package iso.e02.planify.controllers;

// imports de iso.e02.planify
import iso.e02.planify.entities.Schedule;
import iso.e02.planify.requests.WorkScheduleRequest;
import iso.e02.planify.services.WorkScheduleService;

// imports de java
import java.util.List;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestionar las peticiones relacionadas con los horarios
 * laborales.
 * 
 * Esta clase maneja las solicitudes HTTPS para agregar y obtener
 * los horarios de trabajo almacenados en la base de datos.
 * Proporciona métodos para la creación y consulta de horarios laborales.
 */
@RestController
@RequestMapping("workSchedule") // Ruta base para los puntos de acceso de horarios laborales
@CrossOrigin("*")
public class WorkScheduleController {

    @Autowired // Inyección del servicio de horarios laborales
    private WorkScheduleService workScheduleService;

    /**
     * Guarda un nuevo horario laboral en la base de datos.
     * 
     * Este método recibe un objeto {WorkScheduleRequest} desde la solicitud
     * HTTPS, que contiene los datos del horario laboral en forma de lista de bloques.
     * Los bloques horarios son luego procesados y almacenados en la base de datos
     * a través del servicio {WorkScheduleService}.
     * 
     * @param workScheduleInfo el objeto de la solicitud que contiene la lista de bloques
     *                horarios para ser guardados en la base de datos.
     */
    @PostMapping("/addWorkSchedule")
    public void addWorkSchedule(@RequestBody WorkScheduleRequest workScheduleInfo) {
        this.workScheduleService.validateWorkSchedule(workScheduleInfo.getBlocks()); // Validación de los bloques horarios
        this.workScheduleService.saveWorkSchedule(workScheduleInfo.getBlocks()); // Llamada al servicio para guardar los bloques horarios
    }

    /**
     * Obtiene los horarios laborales guardados en la base de datos.
     * 
     * Este método recupera y devuelve una lista de objetos Schedule que representan los horarios laborales almacenados.
     * 
     * @return una lista de objetos Schedule que representan los bloques del horario laboral guardado.
     */
    @GetMapping("/getWorkSchedule")
    public List<Schedule> getWorkSchedule() {
        return this.workScheduleService.getWorkSchedule(); // Llamada al servicio para obtener los bloques horarios
    }
}