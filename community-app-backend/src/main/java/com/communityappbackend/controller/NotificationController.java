package com.communityappbackend.controller;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.dto.NotificationDetailedDTO;
import com.communityappbackend.model.Notification;
import com.communityappbackend.service.NotificationService;
import com.communityappbackend.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.createNotification(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyRaw(Authentication auth) {
        String uid = ((User)auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(uid));
    }

    @GetMapping("/me/detailed")
    public ResponseEntity<List<NotificationDetailedDTO>> getMyDetailed(Authentication auth) {
        String uid = ((User)auth.getPrincipal()).getUserId();
        return ResponseEntity.ok(notificationService.getMyDetailedNotifications(uid));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markRead(
            @PathVariable String notificationId,
            @RequestParam boolean read
    ) {
        return ResponseEntity.ok(
                notificationService.updateNotificationReadStatus(notificationId, read)
        );
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
