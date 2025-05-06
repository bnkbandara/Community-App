package com.communityappbackend.controller.TradeItemsHandleAPIs;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemRequest;
import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.service.TradeItemService.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final ObjectMapper objectMapper;

    // Make sure this matches your actual path
    private static final String ASSETS_DIR =
            "C:\\Projects\\Community App\\community-app-backend\\src\\main\\java\\com\\communityappbackend\\Assets";

    public ItemController(ItemService itemService, ObjectMapper objectMapper) {
        this.itemService = itemService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/add")
    public ItemResponse addItem(
            @RequestPart("item") ItemRequest itemRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) {
        return itemService.addItem(itemRequest, files, auth);
    }

    @GetMapping("/my")
    public List<ItemResponse> getMyItems(Authentication auth) {
        return itemService.getMyItems(auth);
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        File file = new File(ASSETS_DIR, filename);
        if (!file.exists()) return ResponseEntity.notFound().build();
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /**
     * Update an item: JSON in “item” field, optional
     * imageIdsToRemove (CSV), optional new files.
     */
    @PutMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable String itemId,
            @RequestParam("item") String itemJson,
            @RequestParam(value = "imageIdsToRemove", required = false) String imageIdsToRemoveCsv,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) throws Exception {
        ItemRequest request = objectMapper.readValue(itemJson, ItemRequest.class);

        List<Long> removeIds = Collections.emptyList();
        if (imageIdsToRemoveCsv != null && !imageIdsToRemoveCsv.isEmpty()) {
            removeIds = Arrays.stream(imageIdsToRemoveCsv.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }

        ItemResponse updated = itemService.updateItem(itemId, request, removeIds, files, auth);
        return ResponseEntity.ok(updated);
    }

    /** Delete an item and all its files */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable String itemId,
            Authentication auth
    ) {
        itemService.deleteItem(itemId, auth);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public List<ItemResponse> getItemsByUserId(@PathVariable String userId) {
        return itemService.getItemsByOwner(userId);
    }
}
