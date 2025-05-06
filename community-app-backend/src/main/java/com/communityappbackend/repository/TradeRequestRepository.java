package com.communityappbackend.repository;

import com.communityappbackend.model.TradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRequestRepository extends JpaRepository<TradeRequest, String> {

    List<TradeRequest> findByOfferedBy(String offeredBy);
}
