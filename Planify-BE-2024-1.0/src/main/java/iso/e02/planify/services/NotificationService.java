package iso.e02.planify.services;

import iso.e02.planify.entities.Notification;
import iso.e02.planify.entities.Meeting;
import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.repositories.NotificationRepository;
import iso.e02.planify.repositories.MeetingRespository;
import iso.e02.planify.repositories.UsersRepository;
import iso.e02.planify.entities.MeetingAttendance;

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
    public void createMeetingInvitationNotification(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
        CommonUser user = (CommonUser) usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String description = user.getName() + " te ha invitado a la reunión '" + meeting.getSubject() + "'";
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMeeting(meeting);
        notification.setDescription(description);

        notificationRepository.save(notification);
    }

    /**
     * Crea una notificación cuando un usuario acepta o rechaza una invitación.
     */
    public void createResponseNotification(Long meetingId, Long userId, boolean accepted, String reason) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
        CommonUser user = (CommonUser) usersRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String description = user.getName() +
                (accepted
                        ? " ha aceptado tu invitación a la reunión '" + meeting.getSubject() + "'"
                        : " ha rechazado tu invitación a la reunión '" + meeting.getSubject() + "' por el motivo '" + reason + "'");

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMeeting(meeting);
        notification.setDescription(description);

        notificationRepository.save(notification);
    }

    /**
     * Crea notificaciones cuando se cancela una reunión.
     */
    public void createCancellationNotifications(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));

        meeting.getParticipants().stream()
            .filter(attendance -> attendance.getInvitationStatus() == MeetingAttendance.InvitationStatus.ACEPTADA)
            .forEach(attendance -> {
                CommonUser user = attendance.getUser();
                String description = "La reunión '" + meeting.getSubject() + "' ha sido cancelada por el organizador.";

                Notification notification = new Notification();
                notification.setUser(user);
                notification.setMeeting(meeting);
                notification.setDescription(description);

                notificationRepository.save(notification);
            });
    }

    /**
     * Recupera todas las notificaciones de un usuario.
     */
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUser(userId);
    }
}

