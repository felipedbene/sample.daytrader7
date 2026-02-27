package com.ibm.daytrader.web.controller;

import java.time.ZoneOffset;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.daytrader.dto.PricePointDTO;
import com.ibm.daytrader.repository.QuotePriceHistoryRepository;

@RestController
@RequestMapping("/api/charts")
public class ChartDataController {

    private final QuotePriceHistoryRepository priceHistoryRepository;

    public ChartDataController(QuotePriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @GetMapping("/quote/{symbol}")
    public List<PricePointDTO> getQuotePriceHistory(@PathVariable String symbol) {
        return priceHistoryRepository.findBySymbolOrderByRecordedAtAsc(symbol)
                .stream()
                .map(h -> new PricePointDTO(
                        h.getRecordedAt().toEpochSecond(ZoneOffset.UTC),
                        h.getPrice()))
                .toList();
    }
}
