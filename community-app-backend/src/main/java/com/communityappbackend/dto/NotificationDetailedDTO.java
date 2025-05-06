package com.communityappbackend.dto;

import lombok.*;
import java.sql.Timestamp;

/**
 * A richer notification view for clients: includes link back to
 * the original request/donation.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDetailedDTO {
    private String notificationId;
    private String message;
    private Boolean read;
    private String referenceId;
    private String referenceType;
    private Timestamp createdAt;
}
