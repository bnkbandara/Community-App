package com.communityappbackend.dto.TradeItemsHandleDTOs;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {
    private String itemId;
    private String title;
    private String description;
    private Double price;
    private Long categoryId;
    // ⬇︎ New: list of image IDs for removal
    private List<Map<String, Long>> imageList;
    // ⬇︎ URLs for display
    private List<String> images;
    private String status;
    private String createdAt;
    private Long createdAtEpoch;
    private String ownerFullName;
    private String ownerEmail;
    private String ownerPhone;
    private String ownerAddress;
    private String ownerCity;
    private String ownerProvince;
    private String ownerProfileImage;
    private Double ownerAvgRating;
    private Integer ownerReviewCount;
}
