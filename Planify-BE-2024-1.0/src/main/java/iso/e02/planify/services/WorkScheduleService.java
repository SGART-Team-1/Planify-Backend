package iso.e02.planify.services;

// imports de iso.e02.planify
import iso.e02.planify.entities.Schedule;
import iso.e02.planify.repositories.WorkScheduleRepository;

// imports de java
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// imports de spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio para la gestión de horarios laborales.
 * 
 * Esta clase proporciona métodos para procesar listas de bloques de horarios,
 * convertirlas en entidades Schedule y almacenarlas en la base de
 * datos.
 * También permite recuperar todos los horarios almacenados en el repositorio.
 */
@Service
public class WorkScheduleService {
    @Autowired
    private WorkScheduleRepository workScheduleRepository; // Inyección del repositorio de horarios laborales.

    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm"); // Formato de hora    

    private static final String START_HOUR = "startHour"; // Hora de inicio
    private static final String END_HOUR = "endHour"; // Hora de fin

    /**
     * Guarda una lista de bloques de horarios en la base de datos.
     * 
     * @param blocks una lista de mapas que representan los bloques de horarios a guardar.
     */
    public void saveWorkSchedule(List<Map<String, String>> blocks) {
        List<Schedule> schedules = new ArrayList<>();
        blocks.forEach(block -> schedules.add(toSchedule(block))); // Convierte cada bloque a un objeto Schedule.
        this.workScheduleRepository.saveAll(schedules); // Guarda los horarios en la base de datos.
    }

    /**
     * Valida si la lista de bloques de horarios es válida y no se superponen.
     * 
     * @param blocks la lista de bloques de horarios a validar.
     */
    public void validateWorkSchedule(List<Map<String, String>> blocks) {
          if (!getWorkSchedule().isEmpty()) { // validar si la ya existe un horario registrado
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Ya hay un horario registrado.");
        }
        validateNotNull(blocks, "bloques"); // validar si la lista de bloques no es nula
        validateBlocks(blocks); // validar si los bloques no se superponen
    }

    /*
     * Valida si la lista de bloques de horarios no es nula.
     * 
     * @param blocks la lista de bloques de horarios a validar.
     * @param fieldName el nombre del campo a validar.
     */
    public void validateNotNull(List<Map<String, String>> blocks, String fieldName) {
        if (blocks == null || blocks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "El campo \"" + fieldName + "\" es obligatorio.");
        } 
    }

    /**
     * Valida que no sea nulo el nombre de bloque, la hora de inicio y la hora de fin.
     * Valida que la hora de inicio no sea posterior a la hora de fin y la duración del bloque sea de al menos 2 horas.
     * Valida que los bloques no se superpongan.
     * 
     * @param blocks la lista de bloques de horarios a validar.
     */
    public void validateBlocks(List<Map<String, String>> blocks) {
        for (Map<String, String> block : blocks) {
            validateBlockNotNull(block.get("blockName"), "nombre de bloque");
            validateBlockNotNull(block.get(START_HOUR), "hora de inicio");
            validateBlockNotNull(block.get(END_HOUR), "hora de fin");
            isValidTime(block.get(START_HOUR), block.get(END_HOUR)); // validar si las horas son adecuadas
            checkBlockOverlap(blocks); // validar si los bloques no se superponen
        }
    }

    /**
     * Valida si un campo no es nulo o está vacío.
     * 
     * @param field     el campo a validar.
     * @param fieldName el nombre del campo a validar.
     */
    public void validateBlockNotNull(Object field, String fieldName) {
        if (field == null || 
        field instanceof String && ((String) field).trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El campo \"" + fieldName + "\" es obligatorio.");
        }
    }

    /**
     * Valida si la hora de inicio no es posterior a la hora de fin y la duración del bloque es de al menos 2 horas.
     * 
     * @param startHourStr la hora de inicio en formato de cadena.
     * @param endHourStr   la hora de fin en formato de cadena.
     * @return verdadero si la hora de inicio no es posterior a la hora de fin y la duración del bloque es de al menos 2 horas.
     */
    public boolean isValidTime(String startHourStr, String endHourStr) {
        try {
            LocalTime fromTime = parseTime(startHourStr);
            LocalTime toTime = parseTime(endHourStr);
            if(fromTime.isAfter(toTime)) { // validar si la hora de inicio no es posterior a la hora de fin
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "La hora de inicio de bloque no puede ser posterior a la hora de fin.");
            }
            if(fromTime.until(toTime, ChronoUnit.HOURS) < 2) { // validar si la duración del bloque es de al menos 2 horas
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "El bloque de horario debe tener una duración mínima de 2 horas.");
            }
        } catch (DateTimeParseException e) { // validar si la hora tiene un formato válido
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La hora no tiene un formato válido. Se esperaba 'HH:mm'.");
        }
        return true;
    }

     /**
     * Convierte una cadena en una fecha
     * 
     * @param timeStr fecha en formato de cadena
     * @return objeto LocalTime formateado
     */
    public LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, this.timeFormat);
    }

    /**
     * Valida si los bloques de horario se superponen.
     * Itera sobre la lista de bloques de horario y verifica si alguno se superpone con otro.
     * 
     * @param blocks la lista de bloques de horario a validar.
     * @return verdadero si los bloques de horario no se superponen.
     */
    public boolean checkBlockOverlap (List<Map<String, String>> blocks) {
        for (int i = 0; i < blocks.size(); i++) {
            for (int j = i + 1; j < blocks.size(); j++) {
                if (isOverlap(blocks.get(i), blocks.get(j))) { // validar si los bloques se superponen
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "Los bloques de horario no pueden superponerse.");
                }
            }
        }
        return true;
    }

    /**
     * Valida si dos bloques de horario se superponen.
     * 
     * @param block1 el primer bloque de horario.
     * @param block2 el segundo bloque de horario.
     * @return verdadero si los bloques de horario se superponen.
     */
    public boolean isOverlap(Map<String, String> block1, Map<String, String> block2) {
        LocalTime start1 = parseTime(block1.get(START_HOUR));
        LocalTime end1 = parseTime(block1.get(END_HOUR));
        LocalTime start2 = parseTime(block2.get(START_HOUR));
        LocalTime end2 = parseTime(block2.get(END_HOUR));
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Convierte un bloque de horarios a un objeto Schedule.
     * 
     * @param block      el mapa que representa el bloque de horario.
     * @param indexOfBlock el índice único asignado al horario.
     * @return un objeto Schedule que representa el bloque de horario.
     */
    private Schedule toSchedule(Map<String, String> block) {
        Schedule schedule = new Schedule();
        schedule.setBlockName(block.get("blockName"));
        schedule.setStartHour(LocalTime.parse(block.get(START_HOUR)));
        schedule.setEndHour(LocalTime.parse(block.get(END_HOUR)));
        return schedule;
    }

    /**
     * Obtiene todos los horarios almacenados en la base de datos.
     * 
     * @return una lista de objetos Schedule que representan los horarios laborales almacenados.
     */
    public List<Schedule> getWorkSchedule() {
        return this.workScheduleRepository.findAll();
    }
}