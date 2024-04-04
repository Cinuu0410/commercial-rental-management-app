package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class WalletController {
    private final WalletService walletService;


    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallet")
    public String viewWallet(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            // Przekieruj na stronę logowania, jeśli użytkownik nie jest zalogowany
            return "redirect:/login";
        }

        BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
        if (walletBalance == null) {
            walletBalance = BigDecimal.ZERO;
            session.setAttribute("walletBalance", walletBalance);
        }
        // Przekaz informacje o saldzie do modelu
        model.addAttribute("walletBalance", walletBalance);
        model.addAttribute("loggedInUser", loggedInUser);

        // Wyrenderuj widok portfela
        return "wallet_page";
    }




}