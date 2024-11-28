package iso.e02.planify.services;

import java.time.DayOfWeek;
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
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.entities.Schedule;
import iso.e02.planify.entities.Meeting.Location;
import iso.e02.planify.entities.Meeting.Status;
import iso.e02.planify.entities.MeetingAttendance.InvitationStatus;
import iso.e02.planify.requests.ChangeMeetingInvitationStatusRequest;
import iso.e02.planify.requests.CreateMeetingRequest;

@Service
public class ValidateMeetingService {

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private AbsencesService absencesService;

    @Autowired
    private WorkScheduleService workScheduleService;

    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Formato de fecha y
                                                                                                // hora

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato de fecha

    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm"); // Formato de hora

    public boolean validateMeetingInfo(CreateMeetingRequest meetingInfo) {
        // Comprobar datos generales
        validateRequiredFields(meetingInfo);
        isValidDate(meetingInfo.getDate());
        // Comprobar isAllDayLong/DateTime
        if (!meetingInfo.isAllDayLong()) {
            isValidTime(meetingInfo.getFromTime(), meetingInfo.getToTime());
            validateWithSchedule(meetingInfo.getFromTime(), meetingInfo.getToTime());
        }
        // Comprobar si es una ubicación del desplegable. Location sería al final un
        // enum
        if (!meetingInfo.isOnline()) {
            isValidLocation(meetingInfo.getLocation());
        }
        return true;
    }

    /**
     * Valida que todos los campos requeridos no sean nulos ni estén vacíos.
     * 
     * @param meetingInfo petición https como objeto
     * 
     * @return true si toddo está rellenado, false en caso contrario
     *
     */
    public boolean validateRequiredFields(CreateMeetingRequest meetingInfo) {
        validateNotNull(meetingInfo.getSubject(), "asunto");
        validateNotNull(meetingInfo.isAllDayLong(), "día completo");
        validateNotNull(meetingInfo.getDate(), "fecha");
        validateNotNull(meetingInfo.isOnline(), "online");
        validateNotNull(meetingInfo.getParticipants(), "participantes");

        // Comprobación de reunión de día completo
        if (meetingInfo.isAllDayLong()) {
            meetingInfo.setFromTime(null);
            meetingInfo.setToTime(null);
        } else {
            validateNotNull(meetingInfo.getFromTime(), "inicio");
            validateNotNull(meetingInfo.getToTime(), "fin");
        }

        // Comprobación de reunión online
        if (meetingInfo.isOnline()) {
            meetingInfo.setLocation(null);
        } else {
            validateNotNull(meetingInfo.getLocation(), "localización");
        }

        return true;
    }

    /**
     * Valida que un campo no sea nulo ni esté vacío.
     * 
     * @param field     El valor del campo.
     * @param fieldName El nombre del campo (para mensajes de error).
     */
    public void validateNotNull(Object field, String fieldName) {
        if (field == null ||
                (field instanceof String && ((String) field).trim().isEmpty()) ||
                (field instanceof List && ((List<?>) field).isEmpty())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El campo \"" + fieldName + "\" es obligatorio.");
        }
    }

    /**
     * Comprueba si la fecha es válida. Debe ser al menos en el año 2024 y anterior
     * a hoy.
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
                        "La fecha de la reunión debe ser al menos en el año 2024.");
            }

            if (date.compareTo(LocalDate.now()) <= 0) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La fecha de la reunión debe ser posterior a hoy.");
            }

            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de la reunión no puede ser en fin de semana.");
            }

        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La fecha de la reunión no tiene un formato válido. Se esperaba 'yyyy-MM-dd'.");
        }
        return true;
    }

    /**
     * Comprueba si las horas de inicio y fin son válidas. La hora de inicio debe
     * ser anterior a la hora de fin y la duración
     * de la reunión debe ser al menos de 30 minutos.
     * 
     * @param fromTimeStr la hora de inicio
     * 
     * @return true si la fecha es correcta, false en caso contrario.
     */
    public boolean isValidTime(String fromTimeStr, String toTimeStr) {
        try {
            LocalTime fromTime = parseTime(fromTimeStr);
            LocalTime toTime = parseTime(toTimeStr);

            if (fromTime.isAfter(toTime)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La hora de inicio de la reunión debe ser anterior a la hora de fin.");
            }

            if (fromTime.until(toTime, ChronoUnit.MINUTES) < 30) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La duración mínima entre la hora de inicio y la hora de fin debe ser al menos de 30 minutos.");
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
    public LocalDateTime parseDateTime(String dateStr, String timeStr) {
        return LocalDateTime.parse(dateStr + " " + timeStr, this.dateTimeFormat);
    }

    /**
     * Convierte una cadena en una fecha
     * 
     * @param dateStr fecha en formato de cadena
     * @return objeto LocalDate formateado
     */
    public LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, this.dateFormat);
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
     * Comprueba si una cadena pertenece a un valor del Enum Location
     * 
     * @param locationStr la ubicación de la reunión
     * 
     * @return true si la cadena de la ubicación es un valor del Enum, false en caso
     *         contrario
     */
    public boolean isValidLocation(String locationStr) {
        try {
            Location.valueOf(locationStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La reunión no puede tener la ubicación \"" + locationStr + "\".");
        }
        return true;
    }

    public boolean validateWithSchedule(String fromTimeStr, String toTimeStr) {
        // Obtener las horas de la reunión
        LocalTime fromTime = parseTime(fromTimeStr);
        LocalTime toTime = parseTime(toTimeStr);
        List<Schedule> workSchedule = workScheduleService.getWorkSchedule();
        // Validar
        return validateWithSchedule(fromTime, toTime, workSchedule);

    }

    public boolean validateWithSchedule(LocalTime fromTime, LocalTime toTime, List<Schedule> workSchedule) {
        // Verificar que exista al menos un bloque de horario laboral
		if (workSchedule.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No hay horarios laborales configurados.");
		}
	
		// Determinar el rango general del horario laboral
		LocalTime earliestStart = workSchedule.get(0).getStartHour();
		LocalTime latestEnd = workSchedule.get(workSchedule.size() - 1).getEndHour();
				
		// Verificar que la reunión esté dentro del rango general del horario laboral
		if (fromTime.isBefore(earliestStart) || toTime.isAfter(latestEnd)) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "La reunión no está dentro del rango del horario laboral.");
		}
	
		// Verificar solapamiento con al menos un bloque del horario laboral
        
		boolean isInsideAnyBlock = workSchedule.stream().anyMatch(schedule -> {
			LocalTime scheduleStart = schedule.getStartHour();
			LocalTime scheduleEnd = schedule.getEndHour();
	
			// Verificar solapamiento
			return fromTime.compareTo(scheduleStart) >= 0 && toTime.compareTo(scheduleEnd) <= 0;
		});
	
        if (!isInsideAnyBlock) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La reunión se solapa con algún bloque del horario laboral.");
        }
        return true;
    }

    public boolean validateParticipants(Meeting meeting, CommonUser organizer, List<String> participants) {
        validateUserAvailability(organizer, meeting);
        for (String participantEmail : participants) {
            CommonUser participant = this.manageUsersService.getUserByEmail(participantEmail);
            validateParticipantWithBlockedActivated(participant);
        }
        return true;
    }

    public boolean validateUserAvailability(CommonUser user, Meeting meeting) {
        validateParticipantWithBlockedActivated(user);
        List<MeetingAttendance> userMeetings = this.meetingService.getMeetings(user);
        validateParticipantWithMeetings(userMeetings, meeting);
        List<Absence> userAbsences = this.absencesService.list(user.getId());
        validateParticipantWithAbsences(userAbsences, meeting);
        return true;
    }

    public boolean validateParticipantWithBlockedActivated(CommonUser user) {
        if (user.isBlocked() || !user.isActivated()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "No es posible añadir al usuario \"" + user.getEmail() + "\".");
        }
        return true;
    }

    public boolean validateParticipantWithMeetings(List<MeetingAttendance> userMeetings, Meeting meeting) {
        for (MeetingAttendance userMeeting : userMeetings) {
            if (userMeeting.getMeeting().getFromDateTime().isBefore(meeting.getToDateTime()) // Solapamiento
                    && userMeeting.getMeeting().getToDateTime().isAfter(meeting.getFromDateTime())
                        && !userMeeting.getMeeting().getId().equals(meeting.getId()) // Reunión diferente a la actual
                            && !userMeeting.getMeeting().getStatus().equals(Status.CANCELADA)) { // Reunión abierta o cerrada
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La reunión no se puede agregar por solapamiento con otra reunión.");
            }
        }
        return true;
    }

    public boolean validateParticipantWithAbsences(List<Absence> userAbsences, Meeting meeting) {
        for (Absence absence : userAbsences) {
            if (absence.getFromDateTime().isBefore(meeting.getToDateTime())
                    && absence.getToDateTime().isAfter(meeting.getFromDateTime())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                        "La reunión no se puede agregar por solapamiento con una ausencia.");
            }
        }
        return true;
    }

    /**
     * Obtiene un usuario común a partir de la petición HTTPS ya validada en forma
     * de objeto.
     * 
     * @param validatedMeetingInfo la petición HTTPS en forma de objeto ya validada
     *                             en
     *                             cuanto a formato.
     * 
     * @return el usuario
     */
    public Meeting toMeeting(CreateMeetingRequest validatedMeetingInfo) {

        Meeting meeting = new Meeting();
        meeting.setSubject(validatedMeetingInfo.getSubject());
        meeting.setAllDayLong(validatedMeetingInfo.isAllDayLong());
        meeting.setObservations(validatedMeetingInfo.getObservations() == null || validatedMeetingInfo.getObservations().equals("") ? null : validatedMeetingInfo.getObservations());
        meeting.setStatus(Status.ABIERTA);

        if (!validatedMeetingInfo.isAllDayLong()) {
            meeting.setFromDateTime(parseDateTime(validatedMeetingInfo.getDate(), validatedMeetingInfo.getFromTime()));
            meeting.setToDateTime(parseDateTime(validatedMeetingInfo.getDate(), validatedMeetingInfo.getToTime()));
        } else {
            LocalDate meetingDate = parseDate(validatedMeetingInfo.getDate());
            meeting.setFromDateTime(meetingDate.atStartOfDay());
            meeting.setToDateTime(meetingDate.atTime(LocalTime.MAX));
        }

        meeting.setOnline(validatedMeetingInfo.isOnline());
        if (!validatedMeetingInfo.isOnline()) {
            meeting.setLocation(Location.valueOf(validatedMeetingInfo.getLocation().toUpperCase()));
        }

        return meeting;
    }

    /**
     * Obtiene un objeto MeetingAttendance a partir de una reunión y el organizador.
     * 
     * @param meeting   la reunión
     * @param organizer el organizador
     * 
     * @return el objeto MeetingAttendance relativo al organizador
     */
    public MeetingAttendance toOrganizerAttendance(Meeting meeting, CommonUser organizer) {

        MeetingAttendance organizerAttendance = new MeetingAttendance();

        organizerAttendance.setUser(organizer);
        organizerAttendance.setMeeting(null);
        organizerAttendance.setRole(MeetingAttendance.Role.ORGANIZADOR);
        organizerAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.ACEPTADA);
        organizerAttendance.setAssisted(false);
        organizerAttendance.setDeclineReason(null);

        return organizerAttendance;
    }

    // POSIBILIDAD DE TENER validateMeetingAttendanceService
    public boolean validateInvitationStatusChangeInfo(CommonUser user, ChangeMeetingInvitationStatusRequest changeInfo,
            Long meetingId) {
        // Validar que la asistencia a la reunión existe
        validateMeetingAttendanceExists(meetingId, user.getId());
        validateMeetingOpen(meetingId);
        validateIsParticipant(meetingId, user.getId());
        //validar nulos
        validateNotNull(changeInfo.getInvitationStatus(), "estado de invitación");
        isValidInvitationStatus(changeInfo.getInvitationStatus());
        validateModifiedStatus(meetingId, user.getId(), changeInfo);
        // Validar motivo de rechazo
        if (!InvitationStatus.valueOf(changeInfo.getInvitationStatus().toUpperCase())
                .equals(InvitationStatus.RECHAZADA)) {
            changeInfo.setDeclineReason(null);
        } else {
            validateNotNull(changeInfo.getDeclineReason(), "motivo de rechazo");
        }
        return true;
    }

    public boolean isValidInvitationStatus(String invitationStatusStr) {
        try {
            InvitationStatus.valueOf(invitationStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El estado de invitación no puede ser \"" + invitationStatusStr + "\".");
        }
        return true;
    }

    public boolean validateMeetingAttendanceExists(Long meetingId, Long userId) {
        if (this.meetingService.getMeetingAttendance(meetingId, userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se ha encontrado la asistencia a la reunión con id " + meetingId + " del usuario con id "
                            + userId + ".");
        }
        return true;
    }




    public boolean validateIsParticipant(Long meetingId, Long userId) {
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, userId);
        if (!meetingAttendance.getRole().equals(MeetingAttendance.Role.ASISTENTE)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El organizador no puede realizar esta acción.");
        }
        return true;
    }

    public boolean validateIsOrganizer(Long meetingId, Long userId) {
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, userId);
        if (!meetingAttendance.getRole().equals(MeetingAttendance.Role.ORGANIZADOR)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El asistente no puede realizar esta acción.");
        }
        return true;
    }

    public boolean validateModifiedStatus(Long meetingId, Long userId,
            ChangeMeetingInvitationStatusRequest changeInfo) {
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, userId);
        if (meetingAttendance.getInvitationStatus().toString().equalsIgnoreCase(changeInfo.getInvitationStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El estado de invitación ya es " + changeInfo.getInvitationStatus() + ".");
        }

        return true;
    }

    public MeetingAttendance toMeetingAttendance(Long meetingId, CommonUser user,
            ChangeMeetingInvitationStatusRequest changeInfo) {
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, user.getId());
        meetingAttendance.setInvitationStatus(InvitationStatus.valueOf(changeInfo.getInvitationStatus().toUpperCase()));
        if (!InvitationStatus.valueOf(changeInfo.getInvitationStatus().toUpperCase())
                .equals(InvitationStatus.RECHAZADA)) {
            meetingAttendance.setDeclineReason(null);
        } else {
            meetingAttendance.setDeclineReason(changeInfo.getDeclineReason());
        }
        return meetingAttendance;
    }

    public boolean validateAssistChangeInfo(CommonUser user, Long meetingId) {
        // Validar que la asistencia a la reunión existe
        validateMeetingAttendanceExists(meetingId, user.getId());
        validateMeetingOpen(meetingId);
        validateNotAssisted(meetingId, user.getId());
        validateOrganizerAssist(user, meetingId); //si el organizador es el que asiste, la reunión se cierra
        return true;
    }

    public boolean validateNotAssisted(Long meetingId, Long userId) {
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, userId);
        if (meetingAttendance.hasAssisted()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El usuario ya ha registrado su asistencia a la reunión.");
        }
        return true;
    }

    public boolean validateOrganizerAssist(CommonUser user, Long meetingId) {//GONZALO,COMPROBAR QUE FUNCIONA
        MeetingAttendance meetingAttendance = this.meetingService.getMeetingAttendance(meetingId, user.getId());
        if(meetingAttendance.getRole().equals(MeetingAttendance.Role.ORGANIZADOR)) {
            Meeting meeting = this.meetingService.getMeeting(meetingId);
            meeting.setStatus(Meeting.Status.CERRADA);
        }
        return true;
    }

    public boolean validateMeetingOpen(Long meetingId) {
        Meeting meeting = this.meetingService.getMeeting(meetingId);
        if (!meeting.getStatus().equals(Status.ABIERTA)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "La reunión está cerrada o cancelada.");
        }
        return true;
    }

    public boolean validateChangeStatusInfo(CommonUser user, Long meetingId, String status) {
        // Validar que la asistencia a la reunión existe
        validateMeetingAttendanceExists(meetingId, user.getId());
        validateMeetingOpen(meetingId);
        validateIsOrganizer(meetingId, user.getId());
        // Validar estado
        validateNotNull(status, "estado");
        isValidStatus(status);
        validateModifiedStatus(meetingId, status);
        return true;
    }

    public boolean validateModifiedStatus(Long meetingId, String status) {
        Meeting meeting = this.meetingService.getMeeting(meetingId);
        if (Status.valueOf(status.toUpperCase()).equals(meeting.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El estado de la reunión ya es " + status + ".");
        }
        if (Status.valueOf(status.toUpperCase()).equals(Status.CERRADA)){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No puedes cerrar la reunión.");
        }

        return true;
    }

    public boolean isValidStatus(String statusStr) {
        try {
            Status.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "El estado no puede ser \"" + statusStr + "\".");
        }
        return true;
    }
}
