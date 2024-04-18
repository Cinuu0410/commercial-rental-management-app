package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Model.Property;
import com.uwb.commercialrentalmanagementapp.Model.RentalAgreement;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RentalAgreementService rentalAgreementService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UtilitiesPaymentService utilitiesPaymentService;
    @Autowired
    private RentPaymentService rentPaymentService;

    @GetMapping("/admin_panel")
    public String adminPanel(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.ADMINISTRATOR.getRoleName())) {
                // Użytkownik nie ma roli SUPER_USER(ADMINISTRATOR), przekieruj na stronę główną
                return "redirect:/main_page";
            }

            // Dodaj informacje o zalogowanym użytkowniku do modelu
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);

        } else {
            // Użytkownik nie jest zalogowany, przekieruj na stronę logowania
            return "redirect:/login";
        }

        return "admin_panel_page";
    }

    @GetMapping("/admin_panel_manage_users")
    public String adminPanelManageUsers(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.ADMINISTRATOR.getRoleName())) {
                // Użytkownik nie ma roli SUPER_USER(ADMINISTRATOR), przekieruj na stronę główną
                return "redirect:/main_page";
            }

            // Dodaj informacje o zalogowanym użytkowniku do modelu
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);

            List<User> getAllUsers = userService.getAllUsers();
            model.addAttribute("getAllUsers", getAllUsers);
        } else {
            // Użytkownik nie jest zalogowany, przekieruj na stronę logowania
            return "redirect:/login";
        }

        return "admin_panel_manage_users_page";
    }


    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin_panel"; // przekierowuje z powrotem do listy użytkowników
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String username, @RequestParam String password,
                          @RequestParam String firstName, @RequestParam String lastName,
                          @RequestParam String email, @RequestParam UserRole role,
                          RedirectAttributes redirectAttributes) {
        if (userService.userExists(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Użytkownik o podanej nazwie już istnieje.");
            return "redirect:/register";
        }

        String hashedPassword = userService.hashAndSaltPassword(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setRole(role.getRoleName());

        userService.saveUser(newUser);
        redirectAttributes.addFlashAttribute("successMessage", "Pomyślnie dodano użytkownika!");
        return "redirect:/admin_panel_manage_users"; // Lub inna strona docelowa
    }

    @GetMapping("/user_panel")
    public String userPanel(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.WYNAJMUJACY.getRoleName()) && !loggedRole.equals(UserRole.NAJEMCA.getRoleName())) {
                // Użytkownik nie ma roli wynajmujacy lub najemca, przekieruj na stronę główną
                return "redirect:/main_page";
            }

            BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
            if (walletBalance == null) {
                walletBalance = BigDecimal.ZERO;
                session.setAttribute("walletBalance", walletBalance);
            }


            // Dodaj informacje o zalogowanym użytkowniku do modelu
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            List<RentalAgreement> rentalAgreements = rentalAgreementService.getRentalAgreementsForUser(userId);
            List<Property> properties = propertyService.getPropertiesForOwner(userId);
            if (!properties.isEmpty()) {
                Long propertyId = properties.get(0).getPropertyId(); // Pobierz ID pierwszej nieruchomości
                String utilitiesStatus = utilitiesPaymentService.getUtilitiesStatusForProperty(propertyId);
                model.addAttribute("utilitiesStatus", utilitiesStatus);
            }


            // Przekaz informacje o saldzie do modelu
            model.addAttribute("walletBalance", walletBalance);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
            model.addAttribute("rentalAgreements", rentalAgreements);
            model.addAttribute("properties", properties);
        } else {
            // Użytkownik nie jest zalogowany, przekieruj na stronę logowania
            return "redirect:/login";
        }

        return "user_panel_page";
    }

    @GetMapping("/getUtilitiesStatus/{propertyId}")
    @ResponseBody
    public Map<String, Object> getUtilitiesStatus(@PathVariable Long propertyId) {
        Map<String, Object> result = new HashMap<>();
        String status = utilitiesPaymentService.getUtilitiesStatusForProperty(propertyId);
        List<BigDecimal> amounts = utilitiesPaymentService.getUtilitiesAmountsForProperty(propertyId);
        List<String> months = utilitiesPaymentService.getUtilitiesMonthsForProperty(propertyId);
        BigDecimal latestAmount = utilitiesPaymentService.getLatestUtilitiesAmountForProperty(propertyId);
        String paymentMonth = utilitiesPaymentService.getPaymentMonthForProperty(propertyId);
        Long paymentIdForProperty = utilitiesPaymentService.getPaymentIdForProperty(propertyId);


        String rentStatus = rentPaymentService.getStatus(propertyId);

        result.put("status", status);
        result.put("amounts", amounts);
        result.put("months", months);
        result.put("latestAmount", latestAmount);
        result.put("paymentMonth", paymentMonth);
        result.put("paymentIdForProperty", paymentIdForProperty);

        result.put("rentStatus", rentStatus);
        return result;
    }

    @PostMapping("/payUtilities/{paymentId}")
    public String payUtilities(@PathVariable Long paymentId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login"; // Przekieruj na stronę logowania, jeśli użytkownik nie jest zalogowany
        }


        BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
        BigDecimal utilitiesAmount = utilitiesPaymentService.getUtilitiesAmountForPayment(paymentId);
        if (walletBalance == null) {
            walletBalance = BigDecimal.ZERO;
            session.setAttribute("walletBalance", walletBalance);
        }

        if (walletBalance.compareTo(utilitiesAmount) >= 0) {
            // Zaktualizuj stan portfela
            walletService.deductFromBalance(loggedInUser, utilitiesAmount);

            // Uzyskaj bieżący znacznik czasu w milisekundach
            long currentTimeMillis = System.currentTimeMillis();

            // Utwórz obiekt Date na podstawie bieżącego znacznika czasu
            Date paymentDate = new Date(currentTimeMillis);

            // Wywołaj metodę usługi do aktualizacji statusu płatności za media
            utilitiesPaymentService.updateUtilitiesStatusToPaid(paymentId, paymentDate);

            model.addAttribute("message", "Płatność zakończona pomyślnie.");
        } else {
            model.addAttribute("error", "Brak środków w portfelu.");
        }

        // Przekierowanie z powrotem do panelu użytkownika
        return "redirect:/user_panel";
    }
}
