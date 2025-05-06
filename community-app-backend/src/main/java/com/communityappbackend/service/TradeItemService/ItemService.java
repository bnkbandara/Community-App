package com.communityappbackend.service.TradeItemService;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemRequest;
import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.exception.ItemNotFoundException;
import com.communityappbackend.model.Item;
import com.communityappbackend.model.ItemImage;
import com.communityappbackend.model.User;
import com.communityappbackend.repository.ItemImageRepository;
import com.communityappbackend.repository.ItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepo;
    private final ItemImageRepository imageRepo;

    private static final String ASSETS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets";

    public ItemService(ItemRepository itemRepo, ItemImageRepository imageRepo) {
        this.itemRepo = itemRepo;
        this.imageRepo = imageRepo;
    }

    public ItemResponse addItem(ItemRequest request, List<MultipartFile> files, Authentication auth) {
        User user = (User) auth.getPrincipal();
        Item newItem = Item.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(request.getCategoryId())
                .ownerId(user.getUserId())
                .status("ACTIVE")
                .build();
        newItem = itemRepo.save(newItem);

        if (files != null) {
            int count = 0;
            for (MultipartFile f : files) {
                if (count++ >= 5) break;
                String path = saveFileToAssets(f);
                ItemImage img = ItemImage.builder()
                        .imagePath(path)
                        .item(newItem)
                        .build();
                imageRepo.save(img);
            }
        }

        return toItemResponse(newItem);
    }

    public List<ItemResponse> getMyItems(Authentication auth) {
        User me = (User) auth.getPrincipal();
        return itemRepo.findByOwnerId(me.getUserId())
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse getItemDetails(String itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Not found: " + itemId));
        return toItemResponse(item);
    }

    public List<ItemResponse> getItemsByOwner(String userId) {
        return itemRepo.findByOwnerId(userId)
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse updateItem(
            String itemId,
            ItemRequest request,
            List<Long> imageIdsToRemove,
            List<MultipartFile> files,
            Authentication auth
    ) {
        // 1) Load & auth
        User user = (User) auth.getPrincipal();
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Not found: " + itemId));
        if (!item.getOwnerId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to update this item.");
        }

        // 2) Update text fields
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategoryId(request.getCategoryId());

        // 3) Remove selected images (disk + in-memory list)
        if (imageIdsToRemove != null && !imageIdsToRemove.isEmpty()) {
            Iterator<ItemImage> iter = item.getImages().iterator();
            while (iter.hasNext()) {
                ItemImage img = iter.next();
                if (imageIdsToRemove.contains(img.getImageId())) {
                    // delete file
                    String filename = Paths.get(img.getImagePath()).getFileName().toString();
                    File f = new File(ASSETS_DIR, filename);
                    if (f.exists()) f.delete();
                    // remove from JPA collection
                    iter.remove();
                }
            }
        }

        // 4) Add new images (up to 5 total)
        int existingCount = item.getImages().size();
        if (files != null) {
            for (MultipartFile file : files) {
                if (existingCount >= 5) break;
                try {
                    File dir = new File(ASSETS_DIR);
                    if (!dir.exists()) dir.mkdirs();
                    String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    File dest = new File(dir, uniqueName);
                    file.transferTo(dest);

                    ItemImage newImg = ItemImage.builder()
                            .imagePath("Assets/" + uniqueName)
                            .item(item)
                            .build();
                    item.getImages().add(newImg);
                    existingCount++;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save file", e);
                }
            }
        }

        // 5) Persist & return
        Item saved = itemRepo.save(item);
        return toItemResponse(saved);
    }


    public void deleteItem(String itemId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getOwnerId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to delete this item.");
        }
        // delete files
        for (ItemImage img : item.getImages()) {
            String filename = Paths.get(img.getImagePath()).getFileName().toString();
            File f = new File(ASSETS_DIR, filename);
            if (f.exists()) f.delete();
        }
        // delete item and cascade images
        itemRepo.delete(item);
    }

    private String saveFileToAssets(MultipartFile file) {
        try {
            File dir = new File(ASSETS_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Couldn't make " + ASSETS_DIR);
            }
            String name = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File out = new File(dir, name);
            file.transferTo(out);
            return "Assets/" + name;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ItemResponse toItemResponse(Item item) {
        // URLs
        List<String> urls = item.getImages().stream()
                .map(img -> {
                    String fn = img.getImagePath().replace("Assets/", "");
                    return "http://10.0.2.2:8080/api/items/image/" + fn;
                })
                .collect(Collectors.toList());

        // imageList of IDs for front-end removal UI
        List<Map<String, Long>> imageList = item.getImages().stream()
                .map(img -> Collections.singletonMap("imageId", img.getImageId()))
                .collect(Collectors.toList());

        String createdAt = item.getCreatedAt() != null
                ? item.getCreatedAt().toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;

        return ItemResponse.builder()
                .itemId(item.getItemId())
                .title(item.getTitle())
                .description(item.getDescription())
                .price(item.getPrice())
                .categoryId(item.getCategoryId())
                .imageList(imageList)   // ⬅︎ now populated
                .images(urls)
                .status(item.getStatus())
                .createdAt(createdAt)
                .createdAtEpoch(item.getCreatedAt() != null ? item.getCreatedAt().getTime() : 0L)
                .ownerFullName("")      // if unused
                .ownerEmail(null)
                .ownerPhone(null)
                .ownerAddress(null)
                .ownerCity(null)
                .ownerProvince(null)
                .ownerProfileImage(null)
                .ownerAvgRating(null)
                .ownerReviewCount(null)
                .build();
    }
}
