package com.ibm.daytrader.web.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.daytrader.dto.PricePointDTO;
import com.ibm.daytrader.entity.QuoteDataBean;
import com.ibm.daytrader.entity.QuotePriceHistory;
import com.ibm.daytrader.repository.QuotePriceHistoryRepository;
import com.ibm.daytrader.repository.QuoteRepository;

@RestController
@RequestMapping("/api/charts")
public class ChartDataController {

    private final QuotePriceHistoryRepository priceHistoryRepository;
    private final QuoteRepository quoteRepository;

    public ChartDataController(QuotePriceHistoryRepository priceHistoryRepository,
                               QuoteRepository quoteRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.quoteRepository = quoteRepository;
    }

    @GetMapping("/quote/{symbol}")
    public List<PricePointDTO> getQuotePriceHistory(@PathVariable String symbol) {
        List<QuotePriceHistory> history = priceHistoryRepository.findBySymbolOrderByRecordedAtAsc(symbol);

        List<PricePointDTO> points = new ArrayList<>(history.stream()
                .map(h -> new PricePointDTO(
                        h.getRecordedAt().toEpochSecond(ZoneOffset.UTC),
                        h.getPrice()))
                .toList());

        // If sparse data, supplement with quote's open and current price
        QuoteDataBean quote = quoteRepository.findById(symbol).orElse(null);
        if (quote != null) {
            long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            if (points.isEmpty()) {
                // No history at all — create synthetic open + current points
                long openTime = LocalDateTime.now().minusHours(8).toEpochSecond(ZoneOffset.UTC);
                points.add(new PricePointDTO(openTime, quote.getOpen()));
                points.add(new PricePointDTO(now, quote.getPrice()));
            } else if (points.size() == 1) {
                // Only 1 point — prepend the open price before it
                long beforeFirst = points.get(0).time() - 3600; // 1 hour before
                points.add(0, new PricePointDTO(beforeFirst, quote.getOpen()));
                // Also append current price if different timestamp
                if (points.get(points.size() - 1).time() < now) {
                    points.add(new PricePointDTO(now, quote.getPrice()));
                }
            } else {
                // Append current price as latest point
                long lastTime = points.get(points.size() - 1).time();
                if (now > lastTime) {
                    points.add(new PricePointDTO(now, quote.getPrice()));
                }
            }
        }

        return points;
    }
}
