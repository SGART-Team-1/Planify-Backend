package iso.e02.planify.services;

import iso.e02.planify.entities.Notification;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.NotificationRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.entities.MeetingAttendance;
import iso.e02.planify.entities.MeetingAttendance.InvitationStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // Obtener reunión
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
    
        // Obtener usuario invitado
        CommonUser invitedUser = (CommonUser) usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    
        // Crear mensaje usando el nombre del usuario autenticado
        String description = authenticatedUser.getName() + " te ha invitado a la reunión '" + meeting.getSubject() + "'";
    
        // Crear la notificación
        Notification notification = new Notification();
        notification.setUser(invitedUser); // Guardar el destinatario (usuario invitado)
        notification.setMeeting(meeting);
        notification.setDescription(description);
    
        // Guardar en la base de datos
        notificationRepository.save(notification);
    }

    /**
     * Crea una notificación cuando un usuario acepta o rechaza una invitación.
     */
    public void createResponseNotification(Long meetingId, Long userId, boolean accepted, String reason) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        CommonUser user = (CommonUser) usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String description = accepted
                ? user.getName() + " ha aceptado tu invitación a la reunión " + meeting.getSubject()
                : user.getName() + " ha rechazado tu invitación a la reunión " 
                    + meeting.getSubject() + " por motivo " + reason;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMeeting(meeting);
        notification.setDescription(description);
        notificationRepository.save(notification);
    }

    // Notificaciones de cancelación
    public void createCancellationNotifications(Long meetingId, CommonUser organizer) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Meeting not found"));

        List<MeetingAttendance> acceptedParticipants = meeting.getParticipants().stream()
                .filter(attendance -> attendance.getInvitationStatus() == InvitationStatus.ACEPTADA)
                .toList();

        for (MeetingAttendance attendance : acceptedParticipants) {
            Notification notification = new Notification();
            notification.setUser(attendance.getUser());
            notification.setMeeting(meeting);
            notification.setDescription(organizer.getName() + " ha cancelado la reunión " + meeting.getSubject() + " que habías aceptado.");
            notificationRepository.save(notification);
        }
    }

    /**
     * Recupera todas las notificaciones de un usuario.
     */
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser(userId);
    }
}

