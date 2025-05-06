package com.communityappbackend.service.DonationService;

import com.communityappbackend.dto.DonationsHandleDTOs.DonationCompleteDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestSentViewDTO;
import com.communityappbackend.dto.DonationsHandleDTOs.DonationRequestViewDTO;
import com.communityappbackend.dto.NotificationDTO;
import com.communityappbackend.model.DonationItem;
import com.communityappbackend.model.DonationItemImage;
import com.communityappbackend.model.DonationRequest;
import com.communityappbackend.model.User;
import com.communityappbackend.model.UserProfileImage;
import com.communityappbackend.repository.DonationItemImageRepository;
import com.communityappbackend.repository.DonationItemRepository;
import com.communityappbackend.repository.DonationRequestRepository;
import com.communityappbackend.repository.UserProfileImageRepository;
import com.communityappbackend.repository.UserRepository;
import com.communityappbackend.service.NotificationService;
import com.communityappbackend.service.RealTimeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DonationRequestService {

    private final DonationRequestRepository donationRequestRepo;
    private final DonationItemRepository donationItemRepo;
    private final UserRepository userRepo;
    private final DonationItemImageRepository donationItemImageRepo;
    private final UserProfileImageRepository userProfileImageRepo;
    private final NotificationService notificationService;
    private final RealTimeService realTimeService;

    public DonationRequestService(
            DonationRequestRepository donationRequestRepo,
            DonationItemRepository donationItemRepo,
            UserRepository userRepo,
            DonationItemImageRepository donationItemImageRepo,
            UserProfileImageRepository userProfileImageRepo,
            NotificationService notificationService,
            RealTimeService realTimeService
    ) {
        this.donationRequestRepo = donationRequestRepo;
        this.donationItemRepo = donationItemRepo;
        this.userRepo = userRepo;
        this.donationItemImageRepo = donationItemImageRepo;
        this.userProfileImageRepo = userProfileImageRepo;
        this.notificationService = notificationService;
        this.realTimeService = realTimeService;
    }

    /**
     * Creates a donation request by the current user,
     * sends live notification and persists a notification entry.
     */
    public DonationRequest createDonationRequest(DonationRequestDTO dto, Authentication auth) {
        User requester = (User) auth.getPrincipal();
        DonationItem donation = donationItemRepo.findById(dto.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        if (donation.getOwnerId().equals(requester.getUserId())) {
            throw new RuntimeException("Cannot request your own donation.");
        }

        DonationRequest saved = donationRequestRepo.save(
                DonationRequest.builder()
                        .donationId(dto.getDonationId())
                        .requestedBy(requester.getUserId())
                        .message(dto.getMessage())
                        .status("PENDING")
                        .build()
        );

        // Live (WebSocket) notification
        realTimeService.notifyDonationRequest(
                donation.getOwnerId(),
                Map.of(
                        "type", "DONATION_REQUEST",
                        "request", toViewDTO(saved)
                )
        );

        // Persistent notification
        notificationService.createNotification(
                new NotificationDTO(
                        donation.getOwnerId(),
                        requester.getFullName() + " requested “" + donation.getTitle() + "”",
                        saved.getRequestId(),
                        "DONATION_REQUEST"
                )
        );

        return saved;
    }

    /**
     * Returns incoming donation requests for the authenticated user's items.
     */
    public List<DonationRequestViewDTO> getIncomingDonationRequestsView(Authentication auth) {
        User me = (User) auth.getPrincipal();
        return donationItemRepo.findByOwnerId(me.getUserId()).stream()
                .flatMap(d -> donationRequestRepo.findByDonationId(d.getDonationId()).stream())
                .map(this::toViewDTO)
                .collect(Collectors.toList());
    }

    private DonationRequestViewDTO toViewDTO(DonationRequest request) {
        DonationItem donation = donationItemRepo.findById(request.getDonationId()).orElse(null);
        String donationTitle = donation != null ? donation.getTitle() : "Unknown item";
        String donationStatus = donation != null ? donation.getStatus() : "UNKNOWN";
        List<String> donationImages = new ArrayList<>();
        if (donation != null && donation.getImages() != null) {
            for (DonationItemImage img : donation.getImages()) {
                String fn = img.getImagePath().replace("Donations/", "");
                donationImages.add("http://10.0.2.2:8080/api/donations/image/" + fn);
            }
        }

        User requester = userRepo.findById(request.getRequestedBy()).orElse(null);
        String requesterFullName = requester != null ? requester.getFullName() : "Unknown";
        String requesterEmail = requester != null ? requester.getEmail() : "";
        String requesterPhone = requester != null ? requester.getPhone() : "";
        AtomicReference<String> requesterProfile = new AtomicReference<>("");
        if (requester != null) {
            userProfileImageRepo.findByUserId(requester.getUserId())
                    .ifPresent(upi -> requesterProfile.set("http://10.0.2.2:8080/image/" + upi.getImagePath()));
        }

        return DonationRequestViewDTO.builder()
                .requestId(request.getRequestId())
                .donationId(request.getDonationId())
                .message(request.getMessage())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .donationTitle(donationTitle)
                .donationImages(donationImages)
                .donationStatus(donationStatus)
                .requestedBy(request.getRequestedBy())
                .requesterFullName(requesterFullName)
                .requesterEmail(requesterEmail)
                .requesterPhone(requesterPhone)
                .requesterProfile(requesterProfile.get())
                .build();
    }

    /**
     * Accepts a donation request, updates statuses,
     * sends live notification and persists a notification entry.
     */
    public DonationRequest acceptDonationRequest(String requestId, Authentication auth) {
        User owner = (User) auth.getPrincipal();
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        DonationItem donation = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        if (!owner.getUserId().equals(donation.getOwnerId())) {
            throw new RuntimeException("Not authorized.");
        }

        req.setStatus("ACCEPTED");
        donation.setStatus("RESERVED");
        donationItemRepo.save(donation);
        DonationRequest updated = donationRequestRepo.save(req);

        // Live notification
        realTimeService.notifyDonationRequest(
                updated.getRequestedBy(),
                Map.of(
                        "type", "DONATION_REQUEST",
                        "request", toViewDTO(updated)
                )
        );

        // Persistent notification
        notificationService.createNotification(
                new NotificationDTO(
                        updated.getRequestedBy(),
                        owner.getFullName() + " accepted your request for “" + donation.getTitle() + "”",
                        updated.getRequestId(),
                        "DONATION_REQUEST"
                )
        );

        return updated;
    }

    /**
     * Rejects a donation request,
     * sends live notification and persists a notification entry.
     */
    public DonationRequest rejectDonationRequest(String requestId, Authentication auth) {
        User owner = (User) auth.getPrincipal();
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        DonationItem donation = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        if (!owner.getUserId().equals(donation.getOwnerId())) {
            throw new RuntimeException("Not authorized.");
        }

        req.setStatus("REJECTED");
        DonationRequest updated = donationRequestRepo.save(req);

        // Live notification
        realTimeService.notifyDonationRequest(
                updated.getRequestedBy(),
                Map.of(
                        "type", "DONATION_REQUEST",
                        "request", toViewDTO(updated)
                )
        );

        // Persistent notification
        notificationService.createNotification(
                new NotificationDTO(
                        updated.getRequestedBy(),
                        owner.getFullName() + " rejected your request for “" + donation.getTitle() + "”",
                        updated.getRequestId(),
                        "DONATION_REQUEST"
                )
        );

        return updated;
    }

    /**
     * Returns all donation requests sent by the current user.
     */
    public List<DonationRequestSentViewDTO> getSentDonationRequestsView(Authentication auth) {
        User me = (User) auth.getPrincipal();
        return donationRequestRepo.findByRequestedBy(me.getUserId())
                .stream()
                .map(this::toSentViewDTO)
                .collect(Collectors.toList());
    }

    private DonationRequestSentViewDTO toSentViewDTO(DonationRequest req) {
        DonationItem donation = donationItemRepo.findById(req.getDonationId()).orElse(null);
        String title = donation != null ? donation.getTitle() : "Unknown";
        List<String> images = donation != null
                ? donation.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Donations/", "");
                    return "http://10.0.2.2:8080/api/donations/image/" + fn;
                })
                .collect(Collectors.toList())
                : Collections.emptyList();
        String dStatus = donation != null ? donation.getStatus() : "";
        User owner = donation != null
                ? userRepo.findById(donation.getOwnerId()).orElse(null)
                : null;
        String rId = owner != null ? owner.getUserId() : "";
        String rName = owner != null ? owner.getFullName() : "";
        String rEmail = owner != null ? owner.getEmail() : "";
        String rPhone = owner != null ? owner.getPhone() : "";
        AtomicReference<String> rProf = new AtomicReference<>("");
        if (owner != null) {
            userProfileImageRepo.findByUserId(owner.getUserId())
                    .ifPresent(upi -> rProf.set("http://10.0.2.2:8080/image/" + upi.getImagePath()));
        }

        return DonationRequestSentViewDTO.builder()
                .requestId(req.getRequestId())
                .donationId(req.getDonationId())
                .donationTitle(title)
                .donationImages(images)
                .donationStatus(dStatus)
                .message(req.getMessage())
                .status(req.getStatus())
                .createdAt(req.getCreatedAt())
                .receiverId(rId)
                .receiverFullName(rName)
                .receiverEmail(rEmail)
                .receiverPhone(rPhone)
                .receiverProfile(rProf.get())
                .build();
    }

    /**
     * Marks a donation as complete for the current requester.
     */
    public DonationRequest completeDonation(String requestId, Authentication auth) {
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        String currentUser = ((User) auth.getPrincipal()).getUserId();
        if (!currentUser.equals(req.getRequestedBy())) {
            throw new RuntimeException("Not authorized");
        }

        DonationItem item = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        item.setStatus("DONATED");
        donationItemRepo.save(item);

        req.setStatus("COMPLETED");
        return donationRequestRepo.save(req);
    }

    /**
     * Retrieves details for completion screen.
     */
    public DonationCompleteDTO getCompleteDetails(String requestId) {
        DonationRequest req = donationRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        DonationItem item = donationItemRepo.findById(req.getDonationId())
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        User donor = userRepo.findById(item.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> images = item.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Donations/", "");
                    return "http://10.0.2.2:8080/api/donations/image/" + fn;
                })
                .collect(Collectors.toList());

        String profileUrl = userProfileImageRepo
                .findByUserId(donor.getUserId())
                .map(upi -> "http://10.0.2.2:8080/image/" + upi.getImagePath())
                .orElse("");

        return DonationCompleteDTO.builder()
                .donationId(item.getDonationId())
                .title(item.getTitle())
                .images(images)
                .donorId(donor.getUserId())
                .donorFullName(donor.getFullName())
                .donorEmail(donor.getEmail())
                .donorPhone(donor.getPhone())
                .donorProfileImage(profileUrl)
                .build();
    }
}
