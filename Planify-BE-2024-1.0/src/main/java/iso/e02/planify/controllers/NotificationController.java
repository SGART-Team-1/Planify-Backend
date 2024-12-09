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
}
