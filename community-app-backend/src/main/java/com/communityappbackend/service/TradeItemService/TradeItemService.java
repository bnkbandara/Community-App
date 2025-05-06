package com.communityappbackend.service.TradeItemService;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.dto.TradeItemsHandleDTOs.TradeRequestDetailedDTO;
import com.communityappbackend.exception.ItemNotFoundException;
import com.communityappbackend.model.Item;
import com.communityappbackend.model.TradeRequest;
import com.communityappbackend.model.User;
import com.communityappbackend.model.UserProfileImage;
import com.communityappbackend.repository.ItemRepository;
import com.communityappbackend.repository.TradeRequestRepository;
import com.communityappbackend.repository.UserProfileImageRepository;
import com.communityappbackend.repository.UserRepository;
import com.communityappbackend.service.RatingsService.RatingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TradeItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final UserProfileImageRepository userProfileImageRepo;
    private final RatingService ratingService;  // injected


    public TradeItemService(
            ItemRepository itemRepo,
            UserRepository userRepo,
            UserProfileImageRepository userProfileImageRepo,
            RatingService ratingService

    ) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.userProfileImageRepo = userProfileImageRepo;
        this.ratingService = ratingService;

    }
    /**
     * Returns all 'ACTIVE' items NOT owned by current user, **scored** by location similarity
     * and **sorted** by (score desc, createdAt desc).
     */
    public List<ItemResponse> getAllActiveExceptUser(Authentication auth, Long categoryId) {
        // 1) Identify current user & fetch raw items
        User currentUser = (User) auth.getPrincipal();
        String currentUserId = currentUser.getUserId();

        List<Item> raw = (categoryId != null)
                ? itemRepo.findByStatusAndOwnerIdNotAndCategoryId("ACTIVE", currentUserId, categoryId)
                : itemRepo.findByStatusAndOwnerIdNot("ACTIVE", currentUserId);

        // 2) Map to DTOs (includes ownerCity, ownerProvince, createdAtEpoch)
        List<ItemResponse> dtos = raw.stream()
                .map(this::toItemResponseWithOwner)
                .collect(Collectors.toList());

        // 3) Score each DTO by location similarity to currentUser
        Map<ItemResponse, Integer> scores = new HashMap<>();
        for (ItemResponse dto : dtos) {
            scores.put(dto, computeLocationScore(dto, currentUser));
        }

        // 4) Sort descending by score, then descending by createdAtEpoch
        dtos.sort(
                Comparator.<ItemResponse>comparingInt(scores::get).reversed()  // high score first
                        .thenComparing(                                             // then recency
                                Comparator.comparingLong(ItemResponse::getCreatedAtEpoch).reversed()
                        )
        );

        return dtos;
    }

    /** Simple weighted scoring: +3 city match, +2 province match, +1 address overlap */
    private int computeLocationScore(ItemResponse dto, User user) {
        int score = 0;
        if (user.getCity() != null && user.getCity().equalsIgnoreCase(dto.getOwnerCity())) {
            score += 3;
        }
        if (user.getProvince() != null && user.getProvince().equalsIgnoreCase(dto.getOwnerProvince())) {
            score += 2;
        }
        String ua = Optional.ofNullable(user.getAddress()).orElse("").toLowerCase();
        String oa = Optional.ofNullable(dto.getOwnerAddress()).orElse("").toLowerCase();
        for (String tok : ua.split("\\W+")) {
            if (tok.length() > 3 && oa.contains(tok)) {
                score += 1;
                break;
            }
        }
        return score;
    }

    /**
     * Build a full response DTO, pulling owner info and images.
     */
    public ItemResponse getItemDetails(String itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found: " + itemId));
        return toItemResponseWithOwner(item);
    }

    // -- private helper --
    private ItemResponse toItemResponseWithOwner(Item item) {
        List<String> imageUrls = item.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Assets/","");
                    return "http://10.0.2.2:8080/api/items/image/" + fn;
                }).collect(Collectors.toList());

        Timestamp ts = item.getCreatedAt();
        String createdAtStr = ts != null
                ? ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;
        long epoch = ts != null ? ts.getTime() : 0;

        User owner = userRepo.findById(item.getOwnerId()).orElse(null);
        String ofn = "", oe = "", op = "", oa = "", oc = "", oprov = "", opi = "";
        double avg = 0.0;
        int count = 0;

        if (owner != null) {
            ofn    = owner.getFullName();
            oe     = owner.getEmail();
            op     = owner.getPhone();
            oa     = Optional.ofNullable(owner.getAddress()).orElse("");
            oc     = Optional.ofNullable(owner.getCity()).orElse("");
            oprov  = Optional.ofNullable(owner.getProvince()).orElse("");
            Optional<UserProfileImage> upi = userProfileImageRepo.findByUserId(owner.getUserId());
            if (upi.isPresent() && upi.get().getImagePath() != null) {
                String fn = upi.get().getImagePath().replace("Assets/","");
                opi = "http://10.0.2.2:8080/image/" + fn;
            }
            var summary = ratingService.summaryForUser(owner.getUserId());
            avg = summary.getAverage();
            count = summary.getCount();
        }

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .images(imageUrls)
                .status(item.getStatus())
                .createdAt(createdAtStr)
                .createdAtEpoch(epoch)
                .ownerFullName(ofn)
                .ownerEmail(oe)
                .ownerPhone(op)
                .ownerAddress(oa)
                .ownerCity(oc)
                .ownerProvince(oprov)
                .ownerProfileImage(opi)
                .ownerAvgRating(avg)           // NEW
                .ownerReviewCount(count)       // NEW
                .build();
    }

}
