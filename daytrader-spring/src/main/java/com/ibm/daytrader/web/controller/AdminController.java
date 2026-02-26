package com.ibm.daytrader.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.daytrader.dto.RunStatsDTO;
import com.ibm.daytrader.service.DatabaseInitService;
import com.ibm.daytrader.service.TradeService;
import com.ibm.daytrader.service.TradingScenarioService;

@Controller
public class AdminController {

    private final DatabaseInitService databaseInitService;
    private final TradeService tradeService;
    private final TradingScenarioService scenarioService;

    public AdminController(DatabaseInitService databaseInitService, TradeService tradeService,
                           TradingScenarioService scenarioService) {
        this.databaseInitService = databaseInitService;
        this.tradeService = tradeService;
        this.scenarioService = scenarioService;
    }

    @PostMapping("/admin/populate")
    public String populate(Model model) {
        String result = databaseInitService.populateDatabase();
        model.addAttribute("result", result);
        model.addAttribute("config", null);
        model.addAttribute("scenario", scenarioService);
        return "config";
    }

    @PostMapping("/admin/reset")
    public String reset(@RequestParam(defaultValue = "false") boolean deleteAll, Model model) {
        RunStatsDTO stats = tradeService.resetTrade(deleteAll);
        model.addAttribute("stats", stats);
        model.addAttribute("config", null);
        model.addAttribute("scenario", scenarioService);
        return "config";
    }

    @PostMapping("/admin/scenario/start")
    public String startScenario(@RequestParam(defaultValue = "4") int threads,
                                RedirectAttributes redirectAttributes) {
        scenarioService.start(threads);
        redirectAttributes.addFlashAttribute("message",
                "Trading scenario started with " + threads + " threads");
        return "redirect:/config";
    }

    @PostMapping("/admin/scenario/stop")
    public String stopScenario(RedirectAttributes redirectAttributes) {
        scenarioService.stop();
        redirectAttributes.addFlashAttribute("message",
                "Trading scenario stopped. " + scenarioService.getStats());
        return "redirect:/config";
    }
}
