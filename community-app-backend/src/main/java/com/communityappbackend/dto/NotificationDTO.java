package com.communityappbackend.dto;

import lombok.*;

/** DTO for creating notifications. */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDTO {
    private String userId;
    private String message;
    private String referenceId;    // e.g. tradeRequestId or donationRequestId
    private String referenceType;  // e.g. "TRADE_REQUEST" or "DONATION_REQUEST"
}
