package com.ibm.daytrader.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.entity.AccountProfileDataBean;
import com.ibm.daytrader.entity.OrderDataBean;
import com.ibm.daytrader.service.TradeService;
import com.ibm.daytrader.util.FinancialUtils;

@Controller
public class AccountController {

    private final TradeService tradeService;

    public AccountController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/account")
    public String account(Principal principal, Model model) {
        String userID = principal.getName();
        AccountDataBean account = tradeService.getAccountData(userID);
        AccountProfileDataBean profile = tradeService.getAccountProfileData(userID);
        List<OrderDataBean> orders = tradeService.getOrders(userID);

        model.addAttribute("account", account);
        model.addAttribute("profile", profile);
        model.addAttribute("orders", orders);
        model.addAttribute("gain", FinancialUtils.computeGain(account.getBalance(), account.getOpenBalance()));
        model.addAttribute("gainPercent", FinancialUtils.computeGainPercent(account.getBalance(), account.getOpenBalance()));

        return "account";
    }

    @PostMapping("/account/update")
    public String updateAccount(@RequestParam String fullName,
                                @RequestParam String address,
                                @RequestParam String email,
                                @RequestParam String creditCard,
                                @RequestParam(required = false) String password,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        String userID = principal.getName();
        AccountProfileDataBean profile = tradeService.getAccountProfileData(userID);

        profile.setFullName(fullName);
        profile.setAddress(address);
        profile.setEmail(email);
        profile.setCreditCard(creditCard);
        if (password != null && !password.isBlank()) {
            profile.setPassword(password);
        }

        tradeService.updateAccountProfile(profile);
        redirectAttributes.addFlashAttribute("message", "Account profile updated successfully.");
        return "redirect:/account";
    }
}
