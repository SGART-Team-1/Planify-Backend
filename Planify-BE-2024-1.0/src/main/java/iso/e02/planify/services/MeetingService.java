package iso.e02.planify.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.Meeting.Status;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.entities.MeetingAttendance.InvitationStatus;
import iso.e02.planify.repositories.MeetingAttendanceRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.requests.CandidateToMeetinDTO;
import iso.e02.planify.requests.CandidatesRequest;
import iso.e02.planify.requests.InspectUserForMeetingsDTO;
import jakarta.transaction.Transactional;

@Service
public class MeetingService {

    public static final String NOT_ORGANIZER_ERROR = "Solo el organizador puede cambiar el estado de la reunión";

    @Autowired
    private MeetingRespository meetingRepository;

    @Autowired
    private MeetingAttendanceRepository meetingAttendanceRepository;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private NotificationService notificationService;

    public List<Map<String, Object>> listAll(Long userId) {
        return meetingAttendanceRepository.findAllByUserId(userId);
    }

    public Meeting getMeeting(Long meetingId) {
        Optional<Meeting> meetingOptional = meetingRepository.findById(meetingId);
        if (meetingOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Reunión no encontrada");
        }
        return meetingOptional.get();
    }

    public List<MeetingAttendance> getMeetings(CommonUser user) {
        return meetingAttendanceRepository.findByUserId(user.getId());
    }

    public MeetingAttendance getMeetingAttendance(Long meetingId, Long userId) {
        return meetingAttendanceRepository.findByMeetingIdAndUserId(meetingId, userId);
    }

    public List<InspectUserForMeetingsDTO> getMeetingAttendances(Long meetingId) {
        return meetingAttendanceRepository.findAttendeesForMeetingById(meetingId);
    }

    public InspectUserForMeetingsDTO findOrganizadorDetailsById(Long meetingId) {
        return meetingAttendanceRepository.findOrganizadorDetailsById(meetingId);
    }

    @Transactional
    public void create(Meeting meeting, CommonUser organizer, List<String> participantsEmails) {
        List<MeetingAttendance> participants = new ArrayList<>();
    
        // Crear la asistencia del organizador
        MeetingAttendance organizerAttendance = new MeetingAttendance();
        organizerAttendance.setUser(organizer);
        organizerAttendance.setMeeting(meeting);
        organizerAttendance.setRole(MeetingAttendance.Role.ORGANIZADOR);
        organizerAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.ACEPTADA);
        organizerAttendance.setAssisted(false);
        organizerAttendance.setDeclineReason(null);
        participants.add(organizerAttendance);
    
        // Crear asistencias para los demás participantes
        for (String participantEmail : participantsEmails) {
            MeetingAttendance participantAttendance = new MeetingAttendance();
            participantAttendance.setUser(this.manageUsersService.getUserByEmail(participantEmail));
            participantAttendance.setMeeting(meeting);
            participantAttendance.setRole(MeetingAttendance.Role.ASISTENTE);
            participantAttendance.setInvitationStatus(MeetingAttendance.InvitationStatus.PENDIENTE);
            participantAttendance.setAssisted(false);
            participantAttendance.setDeclineReason(null);
            participants.add(participantAttendance);
        }
    
        meeting.setParticipants(participants);
        meetingRepository.save(meeting);
    
        // Crear notificaciones para los asistentes (excluyendo al organizador)
        for (MeetingAttendance participant : participants) {
            if (!participant.getRole().equals(MeetingAttendance.Role.ORGANIZADOR)) {
                notificationService.createMeetingInvitationNotification(
                        meeting.getId(),
                        participant.getUser().getId(),
                        organizer);
            }
        }
    }
    
    @Transactional
    public void editMeeting(Meeting updatedMeeting, Long meetingId, CommonUser organizer, List<String> participants) {

        // Lista con los participantes de la reunión a editar
        List<MeetingAttendance> newParticipants = new ArrayList<>();
        // Lista con los participantes que hay antes de editar la reunión
        List<InspectUserForMeetingsDTO> existingParticipantsList = getMeetingAttendances(meetingId);

        // Conservar los participantes que ya estaban
        for (InspectUserForMeetingsDTO existingParticipant : existingParticipantsList) {
            if (participants.contains(existingParticipant.getEmail())
                    || organizer.getEmail().equals(existingParticipant.getEmail())) {
                MeetingAttendance attendance = getMeetingAttendance(meetingId, existingParticipant.getId());
                newParticipants.add(attendance); // Aunque estuviera, se añade a la nueva lista de participantes
                participants.remove(existingParticipant.getEmail()); // Y se elimina para no tenerlos en cuenta de nuevo
            }
        }

        // Añadir los participantes que no estaban
        for (String newParticipantEmail : participants) {
            CommonUser participant = this.manageUsersService.getUserByEmail(newParticipantEmail);

            MeetingAttendance attendance = new MeetingAttendance();
            attendance.setUser(participant);
            attendance.setMeeting(updatedMeeting);
            attendance.setRole(MeetingAttendance.Role.ASISTENTE);
            attendance.setInvitationStatus(MeetingAttendance.InvitationStatus.PENDIENTE);
            attendance.setAssisted(false);
            attendance.setDeclineReason(null);
            newParticipants.add(attendance);
        }

        // Establecer la nueva lista de participantes
        updatedMeeting.setParticipants(newParticipants);

        // Guardar la reunión y, consecuentemente, los participantes
        this.meetingRepository.save(updatedMeeting);
    }

    @Transactional
    public void assist(CommonUser user, Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        MeetingAttendance attendance = getMeetingAttendance(meeting.getId(), user.getId());
        attendance.setAssisted(true);
        meetingAttendanceRepository.save(attendance);
        // Usar NotificationService para crear la notificación
        notificationService.createAssistanceNotification(attendance.getMeeting(), user);
    }

    @Transactional
    public boolean hasAssisted(CommonUser user, Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        MeetingAttendance attendance = getMeetingAttendance(meeting.getId(), user.getId());
        return attendance.hasAssisted();
    }

    @Transactional
    public InvitationStatus hasAccepted(CommonUser user, Long meetingId) {
        Meeting meeting = getMeeting(meetingId);
        MeetingAttendance attendance = getMeetingAttendance(meeting.getId(), user.getId());
        return attendance.getInvitationStatus();
    }

    @Transactional
    public void changeStatus(Long meetingId, String status, CommonUser organizer) {
        Meeting meeting = getMeeting(meetingId);
    
        // Si el estado es CANCELADA, manejamos la notificación de cancelación
        if ("CANCELADA".equalsIgnoreCase(status)) {
            // Eliminar la reunión si se desea (o mantenerla como cancelada según la lógica actual)
            meeting.setStatus(Meeting.Status.CANCELADA);
            meetingRepository.save(meeting);
    
            // Crear notificaciones para los asistentes que habían aceptado
            notificationService.createCancellationNotifications(meetingId, organizer);
        } else {
            meeting.setStatus(Meeting.Status.valueOf(status));
            meetingRepository.save(meeting);
        }
    }
    

    @Transactional
    public void changeInvitationStatus(MeetingAttendance changedMeetingAttendance, CommonUser user) {
        meetingAttendanceRepository.save(changedMeetingAttendance);
        notificationService.createResponseNotification(
            changedMeetingAttendance.getMeeting().getId(),
            user.getId(),
            changedMeetingAttendance.getInvitationStatus(),
            changedMeetingAttendance.getDeclineReason()
        );
    }

    @Transactional
    public List<CandidateToMeetinDTO> getCandidatesToMeeting(CandidatesRequest request) {
        LocalDateTime fromDateTimeMeeting = parseDateTime(request.getMeetingDate(), request.getFromDateTime());
        LocalDateTime toDateTimeMeeting = parseDateTime(request.getMeetingDate(), request.getToDateTime());
        boolean isAllDay = request.isAllDay();

        List<CandidateToMeetinDTO> registersObtained = meetingRepository.getCandidatesToMeeting();
        return filterCandidates(registersObtained, fromDateTimeMeeting, toDateTimeMeeting, isAllDay);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.parse(date + "T" + time);
    }

    private List<CandidateToMeetinDTO> filterCandidates(
            List<CandidateToMeetinDTO> candidates,
            LocalDateTime fromDateTimeMeeting,
            LocalDateTime toDateTimeMeeting,
            boolean isAllDay) {

        List<CandidateToMeetinDTO> userCandidates = new ArrayList<>();

        for (CandidateToMeetinDTO candidate : candidates) {
            boolean hasOverlap = checkOverlap(
                    candidate, fromDateTimeMeeting, toDateTimeMeeting, isAllDay);

            updateOrAddCandidate(userCandidates, candidate, hasOverlap);
        }
        return userCandidates;
    }

    private boolean checkOverlap(
            CandidateToMeetinDTO candidate,
            LocalDateTime fromDateTimeMeeting,
            LocalDateTime toDateTimeMeeting,
            boolean isAllDay) {

        boolean hasOverlap = false;

        if (candidate.getfromDateTimeAbsence() != null && candidate.gettoDateTimeAbsence() != null) {
            LocalDateTime fromDateRecord = candidate.getfromDateTimeAbsence();
            LocalDateTime toDateRecord = candidate.gettoDateTimeAbsence();

            boolean dateOverlap = !fromDateRecord.toLocalDate().isAfter(fromDateTimeMeeting.toLocalDate()) &&
                    !toDateRecord.toLocalDate().isBefore(toDateTimeMeeting.toLocalDate());

            if (dateOverlap) {
                if (isAllDay ||
                        (fromDateRecord.toLocalTime().equals(LocalTime.MIDNIGHT) &&
                                toDateRecord.toLocalTime().equals(LocalTime.MIDNIGHT))) {
                    hasOverlap = true;
                } else {
                    hasOverlap = fromDateRecord.toLocalTime().isBefore(toDateTimeMeeting.toLocalTime()) &&
                            toDateRecord.toLocalTime().isAfter(fromDateTimeMeeting.toLocalTime());
                }
            }
        }
        return hasOverlap;
    }

    private void updateOrAddCandidate(
            List<CandidateToMeetinDTO> userCandidates,
            CandidateToMeetinDTO candidate,
            boolean hasOverlap) {

        CandidateToMeetinDTO existingUser = userCandidates.stream()
                .filter(c -> c.getId().equals(candidate.getId()))
                .findFirst()
                .orElse(null);

        if (existingUser != null) {
            if (hasOverlap) {
                existingUser.setHasAbsences(true);
            }
        } else {
            candidate.setHasAbsences(hasOverlap);
            userCandidates.add(candidate);
        }
    }

    public boolean hasOpenedMeetings(Long userId) {
        return meetingAttendanceRepository.countOpenedMeetings(userId) > 0;
    }
}
