package com.ibm.daytrader.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.daytrader.config.TradeProperties;

@Controller
public class ConfigController {

    private final TradeProperties tradeProperties;

    public ConfigController(TradeProperties tradeProperties) {
        this.tradeProperties = tradeProperties;
    }

    @GetMapping("/config")
    public String config(Model model) {
        model.addAttribute("config", tradeProperties);
        return "config";
    }

    @PostMapping("/config/update")
    public String updateConfig(@RequestParam(required = false) Integer maxUsers,
                               @RequestParam(required = false) Integer maxQuotes,
                               @RequestParam(required = false) Boolean updateQuotePrices,
                               @RequestParam(required = false) Boolean publishQuotePriceChange,
                               @RequestParam(required = false) Boolean displayOrderAlerts,
                               @RequestParam(required = false) Boolean longRun,
                               @RequestParam(required = false) Integer percentSentToWebsocket,
                               @RequestParam(required = false) Long marketSummaryIntervalMs,
                               RedirectAttributes redirectAttributes) {

        if (maxUsers != null) tradeProperties.setMaxUsers(maxUsers);
        if (maxQuotes != null) tradeProperties.setMaxQuotes(maxQuotes);
        if (updateQuotePrices != null) tradeProperties.setUpdateQuotePrices(updateQuotePrices);
        if (publishQuotePriceChange != null) tradeProperties.setPublishQuotePriceChange(publishQuotePriceChange);
        if (displayOrderAlerts != null) tradeProperties.setDisplayOrderAlerts(displayOrderAlerts);
        if (longRun != null) tradeProperties.setLongRun(longRun);
        if (percentSentToWebsocket != null) tradeProperties.setPercentSentToWebsocket(percentSentToWebsocket);
        if (marketSummaryIntervalMs != null) tradeProperties.setMarketSummaryIntervalMs(marketSummaryIntervalMs);

        redirectAttributes.addFlashAttribute("message", "Configuration updated successfully.");
        return "redirect:/config";
    }
}
