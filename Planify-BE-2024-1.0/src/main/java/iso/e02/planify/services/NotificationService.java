package iso.e02.planify.services;

import iso.e02.planify.entities.Notification;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.NotificationRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.requests.NotificationDTO;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.entities.MeetingAttendance.InvitationStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MeetingRespository meetingRepository;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Crea una notificación cuando se invita a un usuario a una reunión.
     */
    public void createMeetingInvitationNotification(Long meetingId, Long userId, CommonUser authenticatedUser) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
    
        CommonUser invitedUser = (CommonUser) usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    
        String description = authenticatedUser.getName() + " te ha invitado a una reunión: " + meeting.getSubject() + ".";
    
        Notification notification = new Notification();
        notification.setUser(invitedUser); // Destinatario
        notification.setMeeting(meeting);
        notification.setDescription(description);
    
        notificationRepository.save(notification);
    }
    

    /**
     * Crea una notificación cuando un usuario acepta o rechaza una invitación.
     */
    public void createResponseNotification(Long meetingId, Long userId, MeetingAttendance.InvitationStatus status, String reason) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
    
        CommonUser user = (CommonUser) usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        CommonUser organizer = meeting.getParticipants().stream()
                .filter(participant -> participant.getRole() == MeetingAttendance.Role.ORGANIZADOR)
                .map(MeetingAttendance::getUser)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found for the meeting"));
    
        String description;
        if (status == MeetingAttendance.InvitationStatus.ACEPTADA) {
            description = user.getName() + " ha aceptado tu invitación a la reunión " + meeting.getSubject();
        } else if (status == MeetingAttendance.InvitationStatus.RECHAZADA) {
            description = user.getName() + " ha rechazado tu invitación a la reunión " + meeting.getSubject();
            if (reason != null && !reason.isBlank()) {
                description += " por motivo: " + reason;
            }
        } else {
            description = user.getName() + " ha cambiado su estado de invitación a " + status;
        }
    
        Notification notification = new Notification();
        notification.setUser(organizer); // Notificar al organizador
        notification.setMeeting(meeting);
        notification.setDescription(description);
    
        notificationRepository.save(notification);
    }
    

    // Notificaciones de cancelación
    public void createCancellationNotifications(Long meetingId, CommonUser organizer) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
    
        // Filtrar los participantes con estado ACEPTADA, excluyendo al organizador
        List<MeetingAttendance> acceptedParticipants = meeting.getParticipants().stream()
                .filter(attendance -> attendance.getInvitationStatus() == InvitationStatus.ACEPTADA)
                .filter(attendance -> !attendance.getUser().equals(organizer)) // Excluir al organizador
                .toList();
    
        // Crear notificaciones para los asistentes que aceptaron la reunión
        for (MeetingAttendance attendance : acceptedParticipants) {
            Notification notification = new Notification();
            notification.setUser(attendance.getUser());
            notification.setMeeting(meeting);
            notification.setDescription(organizer.getName() + " ha cancelado la reunión " + meeting.getSubject() + " que habías aceptado.");
            notificationRepository.save(notification);
        }
    }
    

    // Crear notificación para cuando un usuario asiste a una reunión
    public void createAssistanceNotification(Meeting meeting, CommonUser user) {
        CommonUser organizer = meeting.getParticipants().stream()
                .filter(participant -> participant.getRole() == MeetingAttendance.Role.ORGANIZADOR)
                .map(MeetingAttendance::getUser)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Organizador no encontrado para la reunión"));

        String description = user.getName() + " ha asistido a la reunión " + meeting.getSubject();

        Notification notification = new Notification();
        notification.setUser(organizer); // Notificar al organizador
        notification.setMeeting(meeting);
        notification.setDescription(description);

        notificationRepository.save(notification);
    }

    /**
     * Recupera todas las notificaciones de un usuario.
     */
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        // Filtrar notificaciones por usuario y convertir a DTO
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notification -> new NotificationDTO(
                        notification.getNotificationId(),
                        notification.getDescription(),
                        notification.getReadingDate()
                ))
                .collect(Collectors.toList());
    }

    public void markAsRead(UUID notificationId) {
        // Buscar notificación y marcar como leída
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setReadingDate(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void discard(UUID notificationId) {
        // Cambiar estado de la notificación a descartada
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setState(true);
        notificationRepository.save(notification);
    }
}
