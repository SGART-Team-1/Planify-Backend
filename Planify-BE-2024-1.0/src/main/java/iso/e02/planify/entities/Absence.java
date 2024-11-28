package iso.e02.planify.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

/**
 * Clase que representa una ausencia registrada por un usuario común.
 * Cada ausencia contiene información sobre el periodo de tiempo y el tipo de ausencia.
 */
@Entity
// @Table(name = "absences") // Comentado pero puede usarse para personalizar el nombre de la tabla.
public class Absence {

    /**
     * Identificador único de la ausencia, generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario común al que pertenece esta ausencia.
     * Relación muchos a uno (varias ausencias pueden pertenecer a un mismo usuario).
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CommonUser commonUser;

    @Column(name = "all_day_long", nullable = false)
    private boolean allDayLong;

    /**
     * Fecha y hora de inicio de la ausencia.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(name = "from_date_time", nullable = false,  columnDefinition = "DATETIME2(0)")
    private LocalDateTime fromDateTime;

    /**
     * Fecha y hora de finalización de la ausencia.
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Column(name = "to_date_time", nullable = false, columnDefinition = "DATETIME2(0)")
    private LocalDateTime toDateTime;

    /**
     * Tipo de ausencia (Vacaciones, baja, registro).
     * Este campo es obligatorio (no puede ser nulo).
     */
    @Enumerated(EnumType.STRING)
    private Type absenceType;

    // Getters y Setters

    /**
     * Obtiene el identificador único de la ausencia.
     * 
     * @return el id de la ausencia.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la ausencia.
     * 
     * @param id el id a establecer para la ausencia.
     */
    public void setId(Long id) {
        this.id = id;
    }


    /**
     * Obtiene el usuario común asociado a esta ausencia.
     * 
     * @return el usuario común asociado.
     */
    
    public Long getUser() {
        return commonUser.getId();
    }


    /**
     * Establece el usuario común al que se asocia esta ausencia.
     * 
     * @param commonUser el usuario común a establecer.
     */
    public void setUser(CommonUser commonUser) {
        this.commonUser = commonUser;
    }

    public Boolean getAllDayLong() {
        return allDayLong;
    }

    public void setAllDayLong(Boolean allDayLong) {
        this.allDayLong = allDayLong;
    }

    /**
     * Obtiene la fecha y hora de inicio de la ausencia.
     * 
     * @return la fecha y hora de inicio.
     */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /**
     * Establece la fecha y hora de inicio de la ausencia.
     * 
     * @param fromDateTime la fecha y hora de inicio a establecer.
     */
    public void setFromDateTime(LocalDateTime fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    /**
     * Obtiene la fecha y hora de finalización de la ausencia.
     * 
     * @return la fecha y hora de finalización.
     */
    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    /**
     * Establece la fecha y hora de finalización de la ausencia.
     * 
     * @param toDateTime la fecha y hora de finalización a establecer.
     */
    public void setToDateTime(LocalDateTime toDateTime) {
        this.toDateTime = toDateTime;
    }

    /**
     * Obtiene el tipo de ausencia registrada.
     * 
     * @return el tipo de ausencia (Vacaciones, baja, permiso).
     */
    public Type getAbsenceType() {
        return absenceType;
    }

    /**
     * Establece el tipo de ausencia.
     * 
     * @param absenceType el tipo de ausencia a establecer.
     */
    public void setAbsenceType(Type absenceType) {
        this.absenceType = absenceType;
    }

    public enum Type {
        VACACIONES, BAJA, PERMISO
    }

}
