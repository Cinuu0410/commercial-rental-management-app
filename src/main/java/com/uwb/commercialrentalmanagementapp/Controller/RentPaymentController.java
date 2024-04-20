package com.uwb.commercialrentalmanagementapp.Controller;


import com.uwb.commercialrentalmanagementapp.Service.RentPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class RentPaymentController {

    @Autowired
    private RentPaymentService rentPaymentService;

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
}
