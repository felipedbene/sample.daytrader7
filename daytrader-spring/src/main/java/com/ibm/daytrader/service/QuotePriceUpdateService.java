package com.ibm.daytrader.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ibm.daytrader.event.QuotePriceChangeEvent;

@Service
public class QuotePriceUpdateService {

    private static final Logger log = LoggerFactory.getLogger(QuotePriceUpdateService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public QuotePriceUpdateService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleQuotePriceChange(QuotePriceChangeEvent event) {
        log.debug("Quote price change: {} {} -> {}", event.getSymbol(), event.getOldPrice(), event.getNewPrice());

        Map<String, Object> payload = Map.of(
                "symbol", event.getSymbol(),
                "oldPrice", event.getOldPrice().toString(),
                "newPrice", event.getNewPrice().toString()
        );

        messagingTemplate.convertAndSend("/topic/market-updates", payload);
    }
}
