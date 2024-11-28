package iso.e02.planify.requests;

/**
 * Clase que representa una solicitud para crear una nueva ausencia de un usuario.
 * Contiene la información necesaria para registrar la ausencia, como el tipo, las fechas y horas de inicio y fin, y el identificador del usuario.
 */
public class CreateAbsenceRequest {

    /** El tipo de ausencia (Vcaciones, baja, permiso) */
    private String absenceType;

    private boolean allDayLong;

    /** La fecha de inicio de la ausencia en formato String */
    private String fromDate;

    /** La hora de inicio de la ausencia en formato String */
    private String fromTime;

    /** La fecha de fin de la ausencia en formato String */
    private String toDate;

    /** La hora de fin de la ausencia en formato String */
    private String toTime;

    /** El identificador del usuario asociado a la ausencia */
    private long userId;

    private boolean overlapsMeeting;

    public CreateAbsenceRequest(String absenceType, Boolean allDayLong, String fromDate, String fromTime, String toDate, String toTime, long userId, boolean overlapsMeeting) { //para nulos trener en el contstructor solo los required y crear setters?
        this.absenceType = absenceType;
        this.allDayLong = allDayLong;
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
        this.userId = userId;
        this.overlapsMeeting = overlapsMeeting;
    }


    
    // Getters y setters
    
    /**
     * Obtiene el identificador del usuario asociado a la ausencia.
     * 
     * @return el identificador del usuario.
     */
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isAllDayLong() {
        return allDayLong;
    }

    public void setAllDayLong(Boolean allDayLong) {
        this.allDayLong = allDayLong;
    }

    /**
     * Obtiene la fecha de inicio de la ausencia.
     * 
     * @return la fecha de inicio en formato String.
     */
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Obtiene la hora de inicio de la ausencia.
     * 
     * @return la hora de inicio en formato String.
     */
    public String getFromTime() {
        return fromTime;
    }

   

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    /**
     * Obtiene la fecha de fin de la ausencia.
     * 
     * @return la fecha de fin en formato String.
     */
    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }


    /**
     * Obtiene la hora de fin de la ausencia.
     * 
     * @return la hora de fin en formato String.
     */
    public String getToTime() {
        return toTime;
    }

    
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    /**
     * Obtiene el tipo de ausencia.
     * 
     * @return el tipo de ausencia.
     */
    public String getAbsenceType() {
        return absenceType;
    }

    public void setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
    }

    public boolean hasOverlapsMeeting() {
        return overlapsMeeting;
    }
}