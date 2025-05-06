package com.communityappbackend.service.TradeItemService;

import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.dto.TradeItemsHandleDTOs.TradeRequestDTO;
import com.communityappbackend.dto.TradeItemsHandleDTOs.TradeRequestDetailedDTO;
import com.communityappbackend.exception.TradeRequestNotFoundException;
import com.communityappbackend.model.Item;
import com.communityappbackend.model.ItemImage;
import com.communityappbackend.model.TradeRequest;
import com.communityappbackend.model.User;
import com.communityappbackend.repository.ItemRepository;
import com.communityappbackend.repository.TradeRequestRepository;
import com.communityappbackend.repository.UserRepository;
import com.communityappbackend.service.NotificationService;
import com.communityappbackend.service.RealTimeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages creation, approval, rejection, and details of trade requests.
 */
@Service
public class TradeRequestService {

    private final TradeRequestRepository tradeRequestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final NotificationService notificationService;
    private final RealTimeService realTimeService;

    public TradeRequestService(
            TradeRequestRepository tradeRequestRepo,
            ItemRepository itemRepo,
            UserRepository userRepo,
            NotificationService notificationService,
            RealTimeService realTimeService
    ) {
        this.tradeRequestRepo = tradeRequestRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.realTimeService = realTimeService;
    }

    /**
     * Creates a trade request (money or item-based).
     */
    /** Creates a trade request and notifies the owner with item name. */
    public TradeRequest createRequest(TradeRequestDTO dto, Authentication auth) {
        User sender = (User) auth.getPrincipal();
        TradeRequest req = TradeRequest.builder()
                .itemId(dto.getItemId())
                .offeredBy(sender.getUserId())
                .moneyOffer(dto.getMoneyOffer() != null ? dto.getMoneyOffer() : 0.0)
                .tradeType(dto.getTradeType() != null ? dto.getTradeType() : "MONEY")
                .status("PENDING")
                .build();
        TradeRequest saved = tradeRequestRepo.save(req);

        // Fetch item to determine owner and title
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Live notify via WebSocket
        realTimeService.notifyTradeRequest(
                item.getOwnerId(),
                Map.of(
                        "type", "TRADE_REQUEST",
                        "request", toDetailedDTO(saved)
                )
        );

        // Persistent notification
        notificationService.createNotification(
                new NotificationDTO(
                        item.getOwnerId(),
                        sender.getFullName() + " requested to trade “" + item.getTitle() + "”",
                        saved.getRequestId(),
                        "TRADE_REQUEST"
                )
        );

        return saved;
    }

    /**
     * Returns all incoming trade requests for items that the current user owns.
     */
    public List<TradeRequest> getIncomingRequests(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Item> myItems = itemRepo.findByOwnerId(user.getUserId());
        Set<String> myItemIds = myItems.stream()
                .map(Item::getItemId)
                .collect(Collectors.toSet());

        return tradeRequestRepo.findAll().stream()
                .filter(r -> myItemIds.contains(r.getItemId()))
                .collect(Collectors.toList());
    }

    /**
     * Approves a request (with optional selected item from the sender).
     */
    /** Approves and notifies with item name. */
    public TradeRequest approveRequest(String requestId, String selectedItemId, Authentication auth) {
        TradeRequest req = tradeRequestRepo.findById(requestId)
                .orElseThrow(() -> new TradeRequestNotFoundException("Trade request not found: " + requestId));
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Cannot approve non-pending request.");
        }
        req.setReceiverSelectedItemId(selectedItemId);
        req.setStatus("ACCEPTED");
        TradeRequest updated = tradeRequestRepo.save(req);

        // Fetch item to determine its title
        Item item = itemRepo.findById(req.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Live notify via WebSocket
        realTimeService.notifyTradeRequest(
                req.getOfferedBy(),
                Map.of(
                        "type", "TRADE_REQUEST",
                        "request", toDetailedDTO(updated)
                )
        );

        // Persistent notification
        User receiver = (User) auth.getPrincipal();
        notificationService.createNotification(
                new NotificationDTO(
                        req.getOfferedBy(),
                        receiver.getFullName() + " accepted your trade request for “" + item.getTitle() + "”",
                        updated.getRequestId(),
                        "TRADE_REQUEST"
                )
        );

        return updated;
    }

    /**
     * Rejects a request and notifies the sender.
     */
    /** Rejects and notifies. */
    public TradeRequest rejectRequest(String requestId, Authentication auth) {
        TradeRequest req = tradeRequestRepo.findById(requestId)
                .orElseThrow(() -> new TradeRequestNotFoundException("Trade request not found: " + requestId));
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Cannot reject non-pending request.");
        }
        req.setStatus("REJECTED");
        TradeRequest updated = tradeRequestRepo.save(req);

        // Fetch item to determine its title
        Item item = itemRepo.findById(req.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Live notify via WebSocket
        realTimeService.notifyTradeRequest(
                req.getOfferedBy(),
                Map.of(
                        "type", "TRADE_REQUEST",
                        "request", toDetailedDTO(updated)
                )
        );

        // Persistent notification
        User receiver = (User) auth.getPrincipal();
        notificationService.createNotification(
                new NotificationDTO(
                        req.getOfferedBy(),
                        receiver.getFullName() + " rejected your trade request for “" + item.getTitle() + "”",
                        updated.getRequestId(),
                        "TRADE_REQUEST"
                )
        );

        return updated;
    }

    /**
     * Returns a detailed list of incoming requests for items owned by the current user,
     * optionally filtered by status.
     */
    public List<TradeRequestDetailedDTO> getIncomingRequestsDetailed(Authentication auth, String status) {
        User me = (User) auth.getPrincipal();
        Set<String> mine = itemRepo.findByOwnerId(me.getUserId())
                .stream().map(Item::getItemId).collect(Collectors.toSet());

        return tradeRequestRepo.findAll().stream()
                .filter(r -> mine.contains(r.getItemId()))
                .filter(r -> status == null || status.isEmpty() || r.getStatus().equalsIgnoreCase(status))
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the publicly listed items of a sender (by userId).
     */
    public List<ItemResponse> getItemsByOwner(String userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }

    // -------------------------- PRIVATE HELPERS -------------------------- //

    private TradeRequestDetailedDTO toDetailedDTO(TradeRequest req) {
        User sender = userRepo.findById(req.getOfferedBy()).orElse(null);
        String offeredByName = (sender != null) ? sender.getFullName() : "Unknown User";
        String senderEmail = (sender != null) ? sender.getEmail() : "";
        String senderPhone = (sender != null) ? sender.getPhone() : "";
        String senderAddress = (sender != null) ? sender.getAddress() : "";

        Item requestedItem = itemRepo.findById(req.getItemId()).orElse(null);
        String requestedTitle = (requestedItem != null) ? requestedItem.getTitle() : "Unknown Item";
        String requestedDescription = (requestedItem != null) ? requestedItem.getDescription() : null;
        Double requestedPrice = (requestedItem != null) ? requestedItem.getPrice() : null;
        List<String> requestedImages = (requestedItem != null)
                ? mapImagesToPaths(requestedItem.getImages())
                : Collections.emptyList();

        // Retrieve the item owner
        User receiver = (requestedItem != null)
                ? userRepo.findById(requestedItem.getOwnerId()).orElse(null)
                : null;

        String receiverUserId = (receiver != null) ? receiver.getUserId() : "";
        String receiverFullName = (receiver != null) ? receiver.getFullName() : "";
        String receiverEmail = (receiver != null) ? receiver.getEmail() : "";
        String receiverPhone = (receiver != null) ? receiver.getPhone() : "";
        String receiverAddress = (receiver != null) ? receiver.getAddress() : "";

        // If it's an item-based trade and the request has a selected item from the sender
        Item offeredItem = null;
        String offeredItemTitle = null;
        String offeredItemDescription = null;
        Double offeredItemPrice = null;
        List<String> offeredItemImages = Collections.emptyList();

        if ("ITEM".equals(req.getTradeType()) && req.getReceiverSelectedItemId() != null) {
            offeredItem = itemRepo.findById(req.getReceiverSelectedItemId()).orElse(null);
            if (offeredItem != null) {
                offeredItemTitle = offeredItem.getTitle();
                offeredItemDescription = offeredItem.getDescription();
                offeredItemPrice = offeredItem.getPrice();
                offeredItemImages = mapImagesToPaths(offeredItem.getImages());
            }
        }

        return TradeRequestDetailedDTO.builder()
                .requestId(req.getRequestId())
                .status(req.getStatus())
                .moneyOffer(req.getMoneyOffer())
                .offeredByUserId(req.getOfferedBy())
                .offeredByUserName(offeredByName)
                .tradeType(req.getTradeType())
                .requestedItemId(req.getItemId())
                .requestedItemTitle(requestedTitle)
                .requestedItemDescription(requestedDescription)
                .requestedItemPrice(requestedPrice)
                .requestedItemImages(requestedImages)
                .offeredItemId(req.getReceiverSelectedItemId())
                .offeredItemTitle(offeredItemTitle)
                .offeredItemDescription(offeredItemDescription)
                .offeredItemPrice(offeredItemPrice)
                .offeredItemImages(offeredItemImages)
                .senderEmail(senderEmail)
                .senderPhone(senderPhone)
                .senderAddress(senderAddress)
                .receiverUserId(receiverUserId)
                .receiverFullName(receiverFullName)
                .receiverEmail(receiverEmail)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .build();
    }

    private List<String> mapImagesToPaths(List<ItemImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(img -> {
                    String fileName = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());
    }

    private ItemResponse toItemResponse(Item item) {
        List<String> imagePaths = item.getImages().stream()
                .map(ItemImage::getImagePath)
                .map(path -> {
                    String fileName = path.replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fileName;
                })
                .collect(Collectors.toList());

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .images(imagePaths)
                .status(item.getStatus())
                .build();
    }

    /**
     * New: Returns all requests the current user has sent, optional status filter.
     */
    public List<TradeRequestDetailedDTO> getOutgoingRequestsDetailed(Authentication auth, String status) {
        User me = (User) auth.getPrincipal();
        return tradeRequestRepo.findByOfferedBy(me.getUserId()).stream()
                .filter(r -> status == null || status.isEmpty() || r.getStatus().equalsIgnoreCase(status))
                .map(this::toDetailedDTO)
                .collect(Collectors.toList());
    }
}
