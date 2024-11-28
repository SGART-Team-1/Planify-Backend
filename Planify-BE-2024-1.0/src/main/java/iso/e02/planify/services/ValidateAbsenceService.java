package iso.e02.planify.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.entities.Absence;
import iso.e02.planify.entities.Absence.Type;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.entities.Schedule;
import iso.e02.planify.repositories.AbsenceRepository;
import iso.e02.planify.repositories.CommonUserRepository;
import iso.e02.planify.repositories.MeetingAttendanceRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.CreateAbsenceRequest;

@Service
public class ValidateAbsenceService {

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private MeetingAttendanceRepository meetingAttendanceRepository;

    @Autowired
    private AbsenceRepository absencesRepository;
    
    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private MeetingRespository meetingRepository;


    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Formato de fecha y hora
    
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato de fecha
    
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm"); // Formato de hora

     public boolean validateAbsenceInfo(CreateAbsenceRequest absenceInfo) {
        validateRequiredFields(absenceInfo);
        validateUserExists(absenceInfo.getUserId());
        validateUnblockedActiveUser(absenceInfo.getUserId());
        isValidAbsenceType(absenceInfo.getAbsenceType());
        isValidDate(absenceInfo.getFromDate());
        isValidDate(absenceInfo.getToDate());
        isValidDateOrder(absenceInfo.getFromDate(), absenceInfo.getToDate());
        // Comprobar isAllDayLong/DateTime
        if(!absenceInfo.isAllDayLong()) {
            isValidTime(absenceInfo.getFromTime(), absenceInfo.getToTime());
        }

        validateAbsenceOverlap(absenceInfo);
        validateAbsenceOverlapsMeetings(absenceInfo);
        return true;
    }

     public boolean validateRequiredFields(CreateAbsenceRequest absenceInfo) {
        validateNotNull(absenceInfo.getAbsenceType(), "tipo de ausencia");
        validateNotNull(absenceInfo.isAllDayLong(), "día completo");
        validateNotNull(absenceInfo.getFromDate(), "fecha inicio");
        validateNotNull(absenceInfo.getToDate(), "fecha fin");
        validateNotNull(absenceInfo.getUserId(), "usuario");

        // Comprobación de ausencia de día completo
        if(absenceInfo.isAllDayLong()) {
            absenceInfo.setFromTime(null);
            absenceInfo.setToTime(null);
        } else {
            validateNotNull(absenceInfo.getFromTime(), "inicio");
            validateNotNull(absenceInfo.getToTime(), "fin");
        }

        return true;
    }

    public void validateNotNull(Object field, String fieldName) {
        if (field == null || 
        field instanceof String && ((String) field).trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El campo \"" + fieldName + "\" es obligatorio.");
        }
    }

    public boolean validateUserExists(long userId) {
        if (!commonUserRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe.");
        }
        return true;
    }

    
    public boolean validateUnblockedActiveUser(long userId) {
        CommonUser user = commonUserRepository.findById(userId);
        if (!user.isActivated() || user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "El usuario no está activo o está bloqueado.");
        }
        return true;
    }
    

    

    public boolean isValidAbsenceType(String absenceTypeStr) {
         try {
            Type.valueOf(absenceTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La ausencia no puede tener el tipo \"" + absenceTypeStr + "\".");
        }
        return true;
    }


      /**
     * Comprueba si la fecha es válida. Debe ser al menos en el año 2024 y anterior a hoy.
     * 
     * @param dateStr la fecha a comprobar
     * 
     * @return true si la fecha es correcta, false en caso contrario.
     */
    public boolean isValidDate(String dateStr) {
        try {
            LocalDate date = parseDate(dateStr);
    
            if (date.isBefore(LocalDate.of(2024, 1, 1))) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de la ausencia debe ser al menos en el año 2024.");
            }
    
            if (date.isBefore(LocalDate.now())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de la ausencia no puede ser anterior al día de hoy.");
            }
    
            if (date.getDayOfWeek().toString().equals("SATURDAY") || date.getDayOfWeek().toString().equals("SUNDAY")) {
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La ausencia no puede ser registrada en fines de semana.");
            }
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de la ausencia no tiene un formato válido. Se esperaba 'yyyy-MM-dd'.");
        }
        return true;
    }

    public boolean isValidDateOrder(String fromDateStr, String toDateStr) {
        LocalDate fromDate = parseDate(fromDateStr);
        LocalDate toDate = parseDate(toDateStr);

        if(fromDate.isAfter(toDate)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de inicio de la ausencia debe ser anterior a la fecha de fin.");
        }
        return true;
    }

      /**
     * Comprueba si las horas de inicio y fin son válidas. La hora de inicio debe ser anterior a la hora de fin y la duración
     * de la ausencia debe ser al menos de 30 minutos.
     * 
     * @param fromTimeStr la hora de inicio
     * 
     * @return true si la fecha es correcta, false en caso contrario.
     */
    public boolean isValidTime(String fromTimeStr, String toTimeStr) {
        try {

            List<Schedule> workSchedule = workScheduleService.getWorkSchedule();
	
		    // Verificar que exista al menos un bloque de horario laboral
		    if (workSchedule.isEmpty()) {
			    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No hay horarios laborales configurados");
		    }
	
		    // Determinar el rango general del horario laboral
		    LocalTime earliestStart = workScheduleService.getWorkSchedule().get(0).getStartHour();
	        LocalTime latestEnd = workScheduleService.getWorkSchedule().get(workScheduleService.getWorkSchedule().size() - 1).getEndHour();

            
            LocalTime fromTime = parseTime(fromTimeStr);
            LocalTime toTime = parseTime(toTimeStr);

            if (fromTime.isBefore(earliestStart) || toTime.isAfter(latestEnd)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "La ausencia no está dentro del rango horario laboral");
            }

            if(fromTime.isAfter(toTime)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "La hora de inicio de la ausencia debe ser anterior a la hora de fin.");
            }

            if(fromTime.until(toTime, ChronoUnit.MINUTES) < 30) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                "La duración mínima entre la hora de inicio y la hora de fin debe ser al menos de 30 minutos.");
            }

            // Verificar solapamiento con al menos un bloque del horario laboral
		    boolean overlapsWithSchedule = workSchedule.stream().anyMatch(schedule -> {
			LocalTime scheduleStart = schedule.getStartHour();
			LocalTime scheduleEnd = schedule.getEndHour();
			return !(toTime.isBefore(scheduleStart) || fromTime.isAfter(scheduleEnd));//mirar
		});
	
		if (!overlapsWithSchedule) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "La ausencia no se solapa con ningún bloque del horario laboral");
		}
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La hora no tiene un formato válido. Se esperaba 'HH:mm'.");
        }
        return true;
    }

      /**
     * Convierte una cadena en una fecha y hora
     * 
     * @param dateStr fecha en formato de cadena
     * @param timeStr hora en formato de cadena
     * 
     * @return fecha y hora formateada
     */
    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return parseDateTime(dateStr);
        }
        return LocalDateTime.parse(dateStr + " " + timeStr, this.dateTimeFormat);
    }

    /**
     * Convierte solo una fecha (sin hora) en un objeto LocalDateTime con la hora ajustada al inicio del día.
     *
     * @param dateStr La fecha en formato "yyyy-MM-dd".
     * 
     * @return Un objeto LocalDateTime que representa la fecha con la hora configurada como 00:00.
     */
    private LocalDateTime parseDateTime(String dateStr) {
        return parseDate(dateStr).atStartOfDay();
    }

    /**
     * Convierte una cadena en una fecha
     * 
     * @param dateStr fecha en formato de cadena
     * @return objeto LocalDate formateado
     */
    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, this.dateFormat);
    }

    /**
     * Convierte una cadena en una fecha
     * 
     * @param timeStr fecha en formato de cadena
     * @return objeto LocalTime formateado
     */
    private LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, this.timeFormat);
    }

    public boolean validateAbsenceOverlap (CreateAbsenceRequest absenceInfo) {
        LocalDateTime fromDateTime = parseDateTime(absenceInfo.getFromDate(), absenceInfo.getFromTime());
        LocalDateTime toDateTime = parseDateTime(absenceInfo.getToDate(), absenceInfo.getToTime());
       
        CommonUser user = commonUserRepository.findById(absenceInfo.getUserId());
        List<Absence> userAbsences = absencesRepository.findByCommonUserId(user.getId());
        for (Absence absence : userAbsences) {
            if ((absence.getFromDateTime().isBefore(toDateTime) && absence.getToDateTime().isAfter(fromDateTime)) ||
             (absenceInfo.isAllDayLong() && absence.getFromDateTime().toLocalDate().isEqual(parseDate(absenceInfo.getFromDate())))) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "El usuario tiene una ausencia en el rango de horas seleccionado.");
            }
        }
        return true;
    }

    public Absence toAbsence(CreateAbsenceRequest absenceInfo) {
        Absence absence = new Absence();
        absence.setAbsenceType(Type.valueOf(absenceInfo.getAbsenceType().toUpperCase()));
        absence.setAllDayLong(absenceInfo.isAllDayLong());
        if(!absenceInfo.isAllDayLong()){
            absence.setFromDateTime(parseDateTime(absenceInfo.getFromDate(), absenceInfo.getFromTime()));
            absence.setToDateTime(parseDateTime(absenceInfo.getToDate(), absenceInfo.getToTime()));
        } else {
            absence.setFromDateTime(parseDateTime(absenceInfo.getFromDate()));
            absence.setToDateTime(parseDateTime(absenceInfo.getToDate()));
        }
        absence.setUser(commonUserRepository.findById(absenceInfo.getUserId()));
        return absence;
    }

    public boolean validateAbsenceExist(long absenceId) {
        if (!absencesRepository.existsById(absenceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La ausencia no existe.");
        }
        return true;
    }

    //metodo para en caso de que se solape con una reunion, cancelarla o rechazarla dependiendo del rol de usuario en esa reunión
    public boolean validateAbsenceOverlapsMeetings(CreateAbsenceRequest absenceInfo) {
        LocalDateTime fromDateTime = parseDateTime(absenceInfo.getFromDate(), absenceInfo.getFromTime());
        LocalDateTime toDateTime = parseDateTime(absenceInfo.getToDate(), absenceInfo.getToTime());
        List<MeetingAttendance> meetingAttendances = meetingAttendanceRepository.findByUserId(absenceInfo.getUserId());
        for (MeetingAttendance meetingAttendance : meetingAttendances) {
            if (meetingAttendance.getMeeting().getFromDateTime().isBefore(toDateTime) && meetingAttendance.getMeeting().getToDateTime().isAfter(fromDateTime)) {
                if (meetingAttendance.getRole().equals(MeetingAttendance.Role.ORGANIZADOR)) {
                    Meeting meeting = meetingRepository.findById(meetingAttendance.getMeeting().getId()).get();
                    meeting.setStatus(Meeting.Status.CANCELADA);
                    meetingRepository.save(meeting);
                } else {
                    meetingAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.RECHAZADA);
                    meetingAttendance.setDeclineReason("El usuario tiene una ausencia planificada.");
                    meetingAttendanceRepository.save(meetingAttendance);
                }
            }
        }
        return true;
    }
}