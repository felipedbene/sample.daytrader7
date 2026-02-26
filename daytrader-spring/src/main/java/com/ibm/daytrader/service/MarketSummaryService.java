package com.ibm.daytrader.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.daytrader.dto.MarketSummaryDTO;
import com.ibm.daytrader.entity.QuoteDataBean;
import com.ibm.daytrader.repository.QuoteRepository;

@Service
public class MarketSummaryService {

    private static final Logger log = LoggerFactory.getLogger(MarketSummaryService.class);

    private final QuoteRepository quoteRepository;
    private volatile MarketSummaryDTO cachedSummary;

    public MarketSummaryService(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Scheduled(fixedDelayString = "${trade.market-summary-interval-ms:20000}")
    @Transactional(readOnly = true)
    public void updateMarketSummary() {
        try {
            List<QuoteDataBean> allQuotes = quoteRepository.findAllOrderByChangeDesc();

            if (allQuotes.isEmpty()) {
                cachedSummary = new MarketSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, 0,
                        List.of(), List.of());
                return;
            }

            // Top 5 gainers (already sorted desc by change)
            List<QuoteDataBean> topGainers = allQuotes.stream().limit(5).toList();

            // Top 5 losers (last 5 in desc order)
            int size = allQuotes.size();
            List<QuoteDataBean> topLosers = allQuotes.stream()
                    .skip(Math.max(0, size - 5))
                    .toList();

            // Compute TSIA (Trade Stock Index Average)
            BigDecimal totalPrice = BigDecimal.ZERO;
            BigDecimal totalOpen = BigDecimal.ZERO;
            double totalVolume = 0;

            for (QuoteDataBean q : allQuotes) {
                if (q.getPrice() != null) totalPrice = totalPrice.add(q.getPrice());
                if (q.getOpen() != null) totalOpen = totalOpen.add(q.getOpen());
                totalVolume += q.getVolume();
            }

            BigDecimal count = new BigDecimal(allQuotes.size());
            BigDecimal tsia = totalPrice.divide(count, 2, RoundingMode.HALF_UP);
            BigDecimal openTSIA = totalOpen.divide(count, 2, RoundingMode.HALF_UP);

            cachedSummary = new MarketSummaryDTO(tsia, openTSIA, totalVolume, topGainers, topLosers);
            log.debug("Market summary updated: TSIA={}", tsia);
        } catch (Exception e) {
            log.error("Error updating market summary", e);
        }
    }

    public MarketSummaryDTO getMarketSummary() {
        if (cachedSummary == null) {
            updateMarketSummary();
        }
        return cachedSummary;
    }
}
