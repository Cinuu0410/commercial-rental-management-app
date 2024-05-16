package com.uwb.commercialrentalmanagementapp.Controller;


import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Model.RentPayment;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.RentPaymentService;
import com.uwb.commercialrentalmanagementapp.Service.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class RentPaymentController {

    @Autowired
    private RentPaymentService rentPaymentService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/createInvoice")
    public String createInvoice(@RequestParam Long propertyId, Model model) {
        log.info("Received request to create invoice for propertyId: {}", propertyId);
        try {
            rentPaymentService.createInvoice(propertyId);
            model.addAttribute("successMessage", "Faktura została wystawiona pomyślnie.");

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie udało się wystawić faktury.");
            log.error("Błąd podczas wystawiania faktury", e);
        }
        return "redirect:/user_panel";
    }

    @GetMapping("/getRentPaymentsForPropertyId")
    @ResponseBody
    public List<RentPayment> getRentPaymentsForPropertyId(@RequestParam Long propertyId) {
        return rentPaymentService.getRentPaymentsForPropertyId(propertyId);
    }

    @PostMapping("/payVat/{paymentId}")
    public String payVatTax(@PathVariable Long paymentId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Przekieruj na stronę logowania, jeśli użytkownik nie jest zalogowany
        }

        // Sprawdzenie istnienia płatności i kwoty VAT
        RentPayment rentPayment = rentPaymentService.getRentPaymentById(paymentId);
        if (rentPayment == null || rentPayment.getVatPaid()) {
            model.addAttribute("error", "Nieprawidłowa płatność lub VAT już odprowadzony.");
            return "redirect:/taxes_page";
        }

        // Obliczenie kwoty VAT
        BigDecimal vatAmount = rentPaymentService.calculateVatAmount(rentPayment.getPaymentAmount());

        BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
        if (walletBalance == null) {
            walletBalance = BigDecimal.ZERO;
            session.setAttribute("walletBalance", walletBalance);
        }

        if (walletBalance.compareTo(vatAmount) >= 0) {
            // Zaktualizuj stan portfela
            walletService.deductFromBalance(loggedInUser, vatAmount);

            // Wywołaj metodę usługi do aktualizacji statusu płatności za media
            rentPaymentService.updateVatPaidStatus(paymentId);

            model.addAttribute("message", "Podatek odprowadzony do US pomyślnie.");
        } else {
            model.addAttribute("error", "Brak środków w portfelu.");
        }

        // Przekierowanie z powrotem do strony z podatkami
        return "redirect:/taxes_page";
    }

    @PostMapping("/calculateIncomeTax")
    @ResponseBody
    public String calculateIncomeTax(HttpSession session, Model model, @RequestParam("totalRevenue") String totalRevenueStr,
                                     @RequestParam("percentageRate") String percentageRateStr) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Przekieruj na stronę logowania, jeśli użytkownik nie jest zalogowany
        }
        BigDecimal totalRevenue = new BigDecimal(totalRevenueStr.replaceAll("[^\\d.]", ""));
        BigDecimal percentageRate = new BigDecimal(percentageRateStr.replaceAll("[^\\d.]", ""));

        BigDecimal incomeTax = rentPaymentService.calculateIncomeTax(totalRevenue, percentageRate);

        BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
        if (walletBalance == null) {
            walletBalance = BigDecimal.ZERO;
            session.setAttribute("walletBalance", walletBalance);
        }
        if (walletBalance.compareTo(incomeTax) >= 0) {
            // Zaktualizuj stan portfela
            walletService.deductFromBalance(loggedInUser, incomeTax);
            model.addAttribute("message", "Podatek dochodowwy został rozliczony pomyślnie. Portfel został zaktualizowany");
        } else {
            model.addAttribute("error", "Brak środków w portfelu.");
        }

        return "redirect:/taxes_page";
    }
}


