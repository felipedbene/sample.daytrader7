package com.ibm.daytrader.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.daytrader.dto.RunStatsDTO;
import com.ibm.daytrader.service.DatabaseInitService;
import com.ibm.daytrader.service.TradeService;

@Controller
public class AdminController {

    private final DatabaseInitService databaseInitService;
    private final TradeService tradeService;

    public AdminController(DatabaseInitService databaseInitService, TradeService tradeService) {
        this.databaseInitService = databaseInitService;
        this.tradeService = tradeService;
    }

    @PostMapping("/admin/populate")
    public String populate(Model model) {
        String result = databaseInitService.populateDatabase();
        model.addAttribute("result", result);
        model.addAttribute("config", null);
        return "config";
    }

    @PostMapping("/admin/reset")
    public String reset(@RequestParam(defaultValue = "false") boolean deleteAll, Model model) {
        RunStatsDTO stats = tradeService.resetTrade(deleteAll);
        model.addAttribute("stats", stats);
        model.addAttribute("config", null);
        return "config";
    }
}
