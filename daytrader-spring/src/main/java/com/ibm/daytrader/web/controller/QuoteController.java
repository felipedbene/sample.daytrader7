package com.ibm.daytrader.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.daytrader.entity.QuoteDataBean;
import com.ibm.daytrader.service.TradeService;

@Controller
public class QuoteController {

    private final TradeService tradeService;

    public QuoteController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/quote")
    public String quotes(@RequestParam(required = false) String symbols, Model model) {
        List<QuoteDataBean> quoteList = new ArrayList<>();

        if (symbols != null && !symbols.isBlank()) {
            String[] symbolArray = symbols.split("[,\\s]+");
            for (String symbol : symbolArray) {
                symbol = symbol.trim();
                if (!symbol.isEmpty()) {
                    QuoteDataBean quote = tradeService.getQuote(symbol);
                    if (quote != null) {
                        quoteList.add(quote);
                    }
                }
            }
        }

        model.addAttribute("quotes", quoteList);
        model.addAttribute("symbols", symbols);
        return "quote";
    }
}
