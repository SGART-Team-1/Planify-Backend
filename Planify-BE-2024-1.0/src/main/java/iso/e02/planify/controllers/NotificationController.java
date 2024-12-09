package iso.e02.planify.controllers;

import iso.e02.planify.entities.CommonUser;
import iso.e02.planify.entities.Notification;
import iso.e02.planify.requests.InvitationNotificationRequest;
import iso.e02.planify.requests.ResponseNotificationRequest;
import iso.e02.planify.services.NotificationService;
import iso.e02.planify.services.JWTService;
import iso.e02.planify.requests.NotificationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("notifications")
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JWTService jwtService;

    // Obtener notificaciones de un usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @RequestHeader("Authorization") String authorizationHeader) {
        Long userId = jwtService.getCommonUserFromJWT(authorizationHeader).getId();
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

        // Marcar notificación como leída
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // Descartar (eliminar lógicamente) una notificación
    @PatchMapping("/{notificationId}/discard")
    public ResponseEntity<Void> discardNotification(@PathVariable UUID notificationId) {
        notificationService.discard(notificationId);
        return ResponseEntity.ok().build();
    }
}
