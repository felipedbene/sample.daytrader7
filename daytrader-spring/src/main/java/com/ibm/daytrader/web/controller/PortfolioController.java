package com.ibm.daytrader.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ibm.daytrader.entity.HoldingDataBean;
import com.ibm.daytrader.service.TradeService;
import com.ibm.daytrader.util.FinancialUtils;

@Controller
public class PortfolioController {

    private final TradeService tradeService;

    public PortfolioController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/portfolio")
    public String portfolio(Principal principal, Model model) {
        String userID = principal.getName();
        List<HoldingDataBean> holdings = tradeService.getHoldings(userID);

        model.addAttribute("holdings", holdings);
        model.addAttribute("holdingsTotal", FinancialUtils.computeHoldingsTotal(holdings));

        return "portfolio";
    }
}
