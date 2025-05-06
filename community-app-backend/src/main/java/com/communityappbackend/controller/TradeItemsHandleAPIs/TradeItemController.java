package com.communityappbackend.controller.TradeItemsHandleAPIs;

import com.communityappbackend.dto.TradeItemsHandleDTOs.ItemResponse;
import com.communityappbackend.service.TradeItemService.TradeItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeItemController {

    private final TradeItemService tradeItemService;

    public TradeItemController(TradeItemService tradeItemService) {
        this.tradeItemService = tradeItemService;
    }

    @GetMapping
    public List<ItemResponse> getAllActiveExceptUser(
            @RequestParam(required = false) Long categoryId,
            Authentication auth
    ) {
        return tradeItemService.getAllActiveExceptUser(auth, categoryId);
    }

    @GetMapping("/details/{itemId}")
    public ResponseEntity<ItemResponse> getItemDetails(@PathVariable String itemId) {
        ItemResponse item = tradeItemService.getItemDetails(itemId);
        return ResponseEntity.ok(item);
    }
}
