package com.uwb.commercialrentalmanagementapp.Controller;


import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Model.RentalAgreement;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.PropertyService;
import com.uwb.commercialrentalmanagementapp.Service.RentPaymentService;
import com.uwb.commercialrentalmanagementapp.Service.RentalAgreementService;
import com.uwb.commercialrentalmanagementapp.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class RentalManager {

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private RentPaymentService rentPaymentService;

    @Autowired
    private RentalAgreementService rentalAgreementService;

    public RentalManager(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/main_page")
    public String mainPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);
            System.out.println(loggedRole);
            // Zapisz informacje o zalogowanym użytkowniku i roli w sesji
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            // Dodaj informacje o zalogowanym użytkowniku do modelu
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
        }
        return "main_page";
    }


    @GetMapping("/login")
    public String loginPage() {
        return "login_page";
    }

    @GetMapping("/contact_us")
    public String contactUsPage(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);
            System.out.println("Zalogowany:" + loggedRole);
            // Zapisz informacje o zalogowanym użytkowniku i roli w sesji
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            // Dodaj informacje o zalogowanym użytkowniku do modelu
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
        }
        return "contact_us_page";
    }

    @GetMapping("/suggestions_and_analyses")
    public String suggestionsAndAnalysesPage(Model model, HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            // Tutaj możemy dodać logikę do pobrania danych o przychodach nieruchomości
            List<Property> properties = propertyService.getPropertiesForOwner(userId);

            // Przygotuj dane o przychodach z nieruchomości
            Map<Property, BigDecimal> propertyIncomes = new HashMap<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Property property : properties) {
                BigDecimal annualIncome = rentPaymentService.getAnnualPaymentAmount(property.getPropertyId());
                propertyIncomes.put(property, annualIncome);
                totalRevenue = totalRevenue.add(annualIncome); // Dodajemy przychód do całkowitej sumy
            }

            // Pobierz umowy najmu użytkownika
            List<RentalAgreement> rentalAgreements = rentalAgreementService.getRentalAgreementsForUser(userId);
            Map<RentalAgreement, BigDecimal> rentalAgreementIncomes = new HashMap<>();

            for (RentalAgreement agreement : rentalAgreements) {
                BigDecimal totalPaymentAmount = rentPaymentService.getAnnualPaymentAmount(agreement.getAgreementId());
                rentalAgreementIncomes.put(agreement, totalPaymentAmount);
            }

            // Przekazujemy dane do widoku
            model.addAttribute("propertyIncomes", propertyIncomes);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("rentalAgreementIncomes", rentalAgreementIncomes);

            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
        }
        return "suggestions_and_analyses_page";
    }



}
