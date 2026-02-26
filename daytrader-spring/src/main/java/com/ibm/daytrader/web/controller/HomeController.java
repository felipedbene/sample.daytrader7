package com.ibm.daytrader.web.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.service.TradeService;
import com.ibm.daytrader.util.FinancialUtils;

@Controller
public class HomeController {

    private final TradeService tradeService;

    public HomeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/tradehome")
    public String tradeHome(Principal principal, Model model) {
        String userID = principal.getName();
        AccountDataBean account = tradeService.getAccountData(userID);
        var holdings = tradeService.getHoldings(userID);

        model.addAttribute("account", account);
        model.addAttribute("holdingsCount", holdings.size());
        model.addAttribute("holdingsTotal", FinancialUtils.computeHoldingsTotal(holdings));
        model.addAttribute("gain", FinancialUtils.computeGain(account.getBalance(), account.getOpenBalance()));
        model.addAttribute("gainPercent", FinancialUtils.computeGainPercent(account.getBalance(), account.getOpenBalance()));

        return "tradehome";
    }
}
