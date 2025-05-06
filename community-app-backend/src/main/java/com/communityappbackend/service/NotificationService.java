package com.communityappbackend.service;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.dto.NotificationDetailedDTO;
import com.communityappbackend.model.Notification;
import com.communityappbackend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public Notification createNotification(NotificationDTO dto) {
        Notification n = Notification.builder()
                .userId(dto.getUserId())
                .message(dto.getMessage())
                .referenceId(dto.getReferenceId())
                .referenceType(dto.getReferenceType())
                .read(false)
                .build();
        return notificationRepo.save(n);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepo.findByUserId(userId);
    }

    public Notification updateNotificationReadStatus(String notificationId, boolean read) {
        Notification n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        n.setRead(read);
        return notificationRepo.save(n);
    }

    public void deleteNotification(String notificationId) {
        if (!notificationRepo.existsById(notificationId)) {
            throw new RuntimeException("Notification not found: " + notificationId);
        }
        notificationRepo.deleteById(notificationId);
    }

    public List<NotificationDetailedDTO> getMyDetailedNotifications(String userId) {
        return notificationRepo.findByUserId(userId).stream()
                .map(n -> NotificationDetailedDTO.builder()
                        .notificationId(n.getNotificationId())
                        .message(n.getMessage())
                        .read(n.getRead())
                        .referenceId(n.getReferenceId())
                        .referenceType(n.getReferenceType())
                        .createdAt(n.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
