package iso.e02.planify.controllers;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Notification;
import iso.e02.planify.requests.InvitationNotificationRequest;
import iso.e02.planify.requests.ResponseNotificationRequest;
import iso.e02.planify.services.NotificationService;
import iso.e02.planify.services.JWTService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JWTService jwtService;

    // Obtener notificaciones de un usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = jwtService.getCommonUserFromJWT(authorizationHeader).getId();
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // Crear notificación de invitación a reunión
    @PostMapping("/invite")
    public ResponseEntity<Void> createInvitationNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody InvitationNotificationRequest request) {
        // Obtener los datos del usuario autenticado
        CommonUser authenticatedUser = jwtService.getCommonUserFromJWT(authorizationHeader);

        // Pasar el ID del usuario invitado y los datos del autenticado al servicio
        notificationService.createMeetingInvitationNotification(
                request.getMeetingId(),
                request.getUserId(), // Destinatario de la notificación
                authenticatedUser // Usuario autenticado
        );

        return ResponseEntity.ok().build();
    }

    // Crear notificación de respuesta a invitación
    @PostMapping("/response")
    public ResponseEntity<Void> createResponseNotification(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ResponseNotificationRequest request) {
        Long userId = jwtService.getCommonUserFromJWT(authorizationHeader).getId();
        notificationService.createResponseNotification(
                request.getMeetingId(), userId, request.isAccepted(), request.getReason());
        return ResponseEntity.ok().build();
    }

    // Crear notificaciones de cancelación para una reunión
    @PostMapping("/cancel/{meetingId}")
    public ResponseEntity<Void> createCancellationNotifications(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long meetingId) {
        CommonUser organizer = jwtService.getCommonUserFromJWT(authorizationHeader);
        notificationService.createCancellationNotifications(meetingId, organizer);
        return ResponseEntity.ok().build();
    }
}
