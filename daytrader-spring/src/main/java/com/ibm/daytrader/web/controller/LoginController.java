package com.ibm.daytrader.web.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.daytrader.entity.AccountDataBean;
import com.ibm.daytrader.service.TradeService;

@Controller
public class LoginController {

    private final TradeService tradeService;

    public LoginController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String userID,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String fullName,
                           @RequestParam String address,
                           @RequestParam String email,
                           @RequestParam String creditCard,
                           @RequestParam(defaultValue = "1000000") String openBalance,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        try {
            AccountDataBean account = tradeService.register(
                    userID, password, fullName, address, email, creditCard,
                    new BigDecimal(openBalance));

            if (account == null) {
                model.addAttribute("error", "User ID '" + userID + "' already exists. Please choose another.");
                return "register";
            }

            redirectAttributes.addFlashAttribute("successMsg",
                    "Registration successful! Your account ID is " + account.getAccountID()
                            + ". Please log in with user ID: " + userID);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}
