package iso.e02.planify.controllers;

import iso.e02.planify.entities.Notification;
import iso.e02.planify.services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> createInvitationNotification(@RequestParam Long meetingId, @RequestParam Long userId) {
        notificationService.createMeetingInvitationNotification(meetingId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/response")
    public ResponseEntity<Void> createResponseNotification(
            @RequestParam Long meetingId, @RequestParam Long userId,
            @RequestParam boolean accepted, @RequestParam(required = false) String reason) {
        notificationService.createResponseNotification(meetingId, userId, accepted, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{meetingId}")
    public ResponseEntity<Void> createCancellationNotifications(@PathVariable Long meetingId) {
        notificationService.createCancellationNotifications(meetingId);
        return ResponseEntity.ok().build();
    }
}

