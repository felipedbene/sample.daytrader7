package com.ibm.daytrader.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.daytrader.entity.QuotePriceHistory;
import com.ibm.daytrader.event.QuotePriceChangeEvent;
import com.ibm.daytrader.repository.QuotePriceHistoryRepository;

@Service
public class QuotePriceUpdateService {

    private static final Logger log = LoggerFactory.getLogger(QuotePriceUpdateService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final QuotePriceHistoryRepository priceHistoryRepository;

    public QuotePriceUpdateService(SimpMessagingTemplate messagingTemplate,
                                   QuotePriceHistoryRepository priceHistoryRepository) {
        this.messagingTemplate = messagingTemplate;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @EventListener
    @Transactional
    public void handleQuotePriceChange(QuotePriceChangeEvent event) {
        log.debug("Quote price change: {} {} -> {}", event.getSymbol(), event.getOldPrice(), event.getNewPrice());

        // Save price history for charts
        priceHistoryRepository.save(
                new QuotePriceHistory(event.getSymbol(), event.getNewPrice(), LocalDateTime.now()));

        // Broadcast via WebSocket
        Map<String, Object> payload = Map.of(
                "symbol", event.getSymbol(),
                "oldPrice", event.getOldPrice().toString(),
                "newPrice", event.getNewPrice().toString()
        );

        messagingTemplate.convertAndSend("/topic/market-updates", payload);
    }

    @Scheduled(fixedRate = 3600000) // every hour
    @Transactional
    public void cleanupOldPriceHistory() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deleted = priceHistoryRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Cleaned up {} old price history records", deleted);
        }
    }
}
