package com.ibm.daytrader.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ibm.daytrader.dto.MarketSummaryDTO;
import com.ibm.daytrader.service.MarketSummaryService;
import com.ibm.daytrader.util.FinancialUtils;

@Controller
public class MarketSummaryController {

    private final MarketSummaryService marketSummaryService;

    public MarketSummaryController(MarketSummaryService marketSummaryService) {
        this.marketSummaryService = marketSummaryService;
    }

    @GetMapping("/marketsummary")
    public String marketSummary(Model model) {
        MarketSummaryDTO summary = marketSummaryService.getMarketSummary();
        if (summary != null) {
            model.addAttribute("summary", summary);
            if (summary.getTSIA() != null && summary.getOpenTSIA() != null) {
                model.addAttribute("tsiaChange",
                        FinancialUtils.computeGain(summary.getTSIA(), summary.getOpenTSIA()));
                model.addAttribute("tsiaChangePercent",
                        FinancialUtils.computeGainPercent(summary.getTSIA(), summary.getOpenTSIA()));
            }
        }
        return "fragments/market-summary :: marketSummary";
    }
}
