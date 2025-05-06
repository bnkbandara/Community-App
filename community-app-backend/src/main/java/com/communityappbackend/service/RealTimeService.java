package com.communityappbackend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class RealTimeService {
    private final SimpMessagingTemplate ws;
    public RealTimeService(SimpMessagingTemplate ws) {
        this.ws = ws;
    }

    public void notifyTradeRequest(String userId, Map<String,Object> payload) {
        ws.convertAndSendToUser(userId, "/queue/trade-requests", payload);
    }

    public void notifyDonationRequest(String userId, Map<String,Object> payload) {
        ws.convertAndSendToUser(userId, "/queue/donation-requests", payload);
    }
}
