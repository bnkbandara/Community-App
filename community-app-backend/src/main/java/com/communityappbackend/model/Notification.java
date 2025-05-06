package com.communityappbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @Column(name = "notification_id", columnDefinition = "CHAR(36)")
    private String notificationId;

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String userId;

    @Column(nullable = false)
    private String message;

    @Column(name = "`read`")
    @Builder.Default
    private Boolean read = false;

    @Column(name = "reference_id", columnDefinition = "CHAR(36)")
    private String referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void generateId() {
        if (notificationId == null) {
            this.notificationId = UUID.randomUUID().toString();
        }
    }
}
