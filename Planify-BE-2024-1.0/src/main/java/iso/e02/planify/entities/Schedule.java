package iso.e02.planify.entities;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad que representa un horario laboral en el sistema.
 * 
 * Esta clase define los atributos básicos de un horario laboral, como el ID,
 * el nombre del bloque, la hora de inicio y la hora de fin. Cada instancia de
 * esta entidad corresponde a un registro en la tabla de horarios en la base de
 * datos.
 */

@Entity
public class Schedule {

    /**
     * Identificador único del horario. Actúa como clave primaria en la base de
     * datos.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del bloque de horario, como "Mañana", "Tarde", etc.
     * Este campo es obligatorio.
     */

    @Column(nullable = false)
    private String blockName;

    /**
     * Hora de inicio del bloque de horario en formato de cadena (por ejemplo,
     * "08:00").
     * Este campo es obligatorio.
     */

    @Column(nullable = false, columnDefinition = "TIME(0)")
    private LocalTime startHour;

    /**
     * Hora de fin del bloque de horario en formato de cadena (por ejemplo,
     * "17:00").
     * Este campo es obligatorio.
     */

    @Column(nullable = false, columnDefinition = "TIME(0)")
    private LocalTime endHour;

    /**
     * Constructor predeterminado de un bloque horario
     */
    public Schedule() {

    }

    /**
     * Constructor de un bloque horario
     */
    public Schedule(Long id, String blockName, LocalTime startHour, LocalTime endHour) {
        this.id = id;
        this.blockName = blockName;
        this.startHour = startHour;
        this.endHour = endHour;
    }


    // Getters y Setters públicos


    /**
     * Obtiene el identificador único del bloque horario.
     * 
     * @return el id del bloque horario.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del bloque horario.
     * 
     * @param id el id a establecer para el bloque horario.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del bloque de horario.
     * 
     * @return el nombre del bloque de horario.
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * Establece el nombre del bloque de horario.
     * 
     * @param blockName el nombre del bloque de horario.
     */
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    /**
     * Obtiene la hora de inicio del bloque de horario.
     * 
     * @return la hora de inicio del bloque.
     */
    public LocalTime getStartHour() {
        return startHour;
    }

    /**
     * Establece la hora de inicio del bloque de horario.
     * 
     * @param startHour la hora de inicio del bloque.
     */
    public void setStartHour(LocalTime startHour) {
        this.startHour = startHour;
    }

    /**
     * Obtiene la hora de fin del bloque de horario.
     * 
     * @return la hora de fin del bloque.
     */
    public LocalTime getEndHour() {
        return endHour;
    }

    /**
     * Establece la hora de fin del bloque de horario.
     * 
     * @param endHour la hora de fin del bloque.
     */
    public void setEndHour(LocalTime endHour) {
        this.endHour = endHour;
    }

}
