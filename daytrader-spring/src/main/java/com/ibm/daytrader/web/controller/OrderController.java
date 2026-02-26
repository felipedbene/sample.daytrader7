package com.ibm.daytrader.web.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.daytrader.entity.OrderDataBean;
import com.ibm.daytrader.service.TradeService;

@Controller
public class OrderController {

    private final TradeService tradeService;

    public OrderController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    public String buy(@RequestParam String symbol,
                      @RequestParam double quantity,
                      Principal principal,
                      Model model) {
        String userID = principal.getName();
        try {
            OrderDataBean order = tradeService.buy(userID, symbol, quantity);
            model.addAttribute("order", order);
            model.addAttribute("message", "Buy order placed successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Buy failed: " + e.getMessage());
        }
        return "order";
    }

    @PostMapping("/sell")
    public String sell(@RequestParam Integer holdingID,
                       Principal principal,
                       Model model) {
        String userID = principal.getName();
        try {
            OrderDataBean order = tradeService.sell(userID, holdingID);
            model.addAttribute("order", order);
            model.addAttribute("message", "Sell order placed successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Sell failed: " + e.getMessage());
        }
        return "order";
    }
}
