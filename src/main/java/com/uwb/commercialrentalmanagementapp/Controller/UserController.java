package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Model.*;
import com.uwb.commercialrentalmanagementapp.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

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
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.ADMINISTRATOR.getRoleName())) {
                return "redirect:/main_page";
            }

            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);

        } else {
            return "redirect:/login";
        }

        return "admin_panel_page";
    }

    @GetMapping("/admin_panel_manage_users")
    public String adminPanelManageUsers(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.ADMINISTRATOR.getRoleName())) {
                return "redirect:/main_page";
            }

            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);

            List<User> getAllUsers = userService.getAllUsers();
            model.addAttribute("getAllUsers", getAllUsers);
        } else {
            return "redirect:/login";
        }

        return "admin_panel_manage_users_page";
    }

    @GetMapping("/editUser/{userId}")
    public String showEditUserForm(@PathVariable Long userId, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            Long loggedInUserIduserId = loggedInUser.getId();
            String loggedRole = userService.getRole(loggedInUserIduserId);

            if (!loggedRole.equals(UserRole.ADMINISTRATOR.getRoleName())) {
                return "redirect:/main_page";
            }
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "edit_user_form_page";
        }else {
                return "redirect:/login";
            }

    }

    @PostMapping("/editUser/{userId}")
    public String editUser(@PathVariable Long userId,
                           @RequestParam String username,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam UserRole role,
                           RedirectAttributes redirectAttributes){

            User user = userService.getUserById(userId);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Użytkownik nie istnieje.");
                return "redirect:/admin_panel";
            }

            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setRole(role.getRoleName());

            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Pomyślnie zaktualizowano użytkownika!");
            return "redirect:/admin_panel_manage_users";
        }

    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Long userId,RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = "Wystąpił błąd: " + e.getMessage();
            System.out.println(errorMessage);
            redirectAttributes.addFlashAttribute("error", "Nie udało się usunąć użytkownika.");
            return "redirect:/admin_panel_manage_users";
        }
        return "redirect:/admin_panel";
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
        return "redirect:/admin_panel_manage_users";
    }

    @GetMapping("/user_panel")
    public String userPanel(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.WYNAJMUJACY.getRoleName()) && !loggedRole.equals(UserRole.NAJEMCA.getRoleName())) {
                return "redirect:/main_page";
            }

            BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
            if (walletBalance == null) {
                walletBalance = BigDecimal.ZERO;
                session.setAttribute("walletBalance", walletBalance);
            }


            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            List<RentalAgreement> rentalAgreements = rentalAgreementService.getRentalAgreementsForUser(userId);
            List<Property> properties = propertyService.getPropertiesForOwner(userId);
            if (!properties.isEmpty()) {
                Long propertyId = properties.get(0).getPropertyId();
                String utilitiesStatus = utilitiesPaymentService.getUtilitiesStatusForProperty(propertyId);
                model.addAttribute("utilitiesStatus", utilitiesStatus);
            }

            model.addAttribute("walletBalance", walletBalance);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
            model.addAttribute("rentalAgreements", rentalAgreements);
            model.addAttribute("properties", properties);
        } else {
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


//        String rentStatus = rentPaymentService.getStatus(propertyId);
        //Do faktury:
        String invoiceFirstLastNameLandlord = rentPaymentService.getFirstAndLastNameOfLandlord(propertyId);
        String invoiceAddressLandlord = rentPaymentService.getAddressOfLandlord(propertyId);
        String invoiceFirstLastNameTenant = rentPaymentService.getFirstAndLastNameOfTenant(propertyId);
        String invoiceAddressTenant = rentPaymentService.getAddressOfTenant(propertyId);
        BigDecimal invoiceRentAmount = rentPaymentService.getRentAmount(propertyId);

//        String rentPaymentDate = rentPaymentService.getPaymentDate(propertyId);
//        String rentNextInvoiceIssueDate = rentPaymentService.getNextInvoiceIssueDate(propertyId);

        result.put("status", status);
        result.put("amounts", amounts);
        result.put("months", months);
        result.put("latestAmount", latestAmount);
        result.put("paymentMonth", paymentMonth);
        result.put("paymentIdForProperty", paymentIdForProperty);

//        result.put("rentStatus", rentStatus);
        //Do faktury:
        result.put("invoiceFirstLastNameLandlord", invoiceFirstLastNameLandlord);
        result.put("invoiceAddressLandlord", invoiceAddressLandlord);
        result.put("invoiceFirstLastNameTenant", invoiceFirstLastNameTenant);
        result.put("invoiceAddressTenant", invoiceAddressTenant);
        result.put("invoiceRentAmount", invoiceRentAmount);

//        result.put("rentPaymentDate", rentPaymentDate);
//        result.put("rentNextInvoiceIssueDate", rentNextInvoiceIssueDate);
        return result;
    }

    @PostMapping("/payUtilities/{paymentId}")
    public String payUtilities(@PathVariable Long paymentId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }


        BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
        BigDecimal utilitiesAmount = utilitiesPaymentService.getUtilitiesAmountForPayment(paymentId);
        if (walletBalance == null) {
            walletBalance = BigDecimal.ZERO;
            session.setAttribute("walletBalance", walletBalance);
        }

        if (walletBalance.compareTo(utilitiesAmount) >= 0) {
            walletService.deductFromBalance(loggedInUser, utilitiesAmount);
            long currentTimeMillis = System.currentTimeMillis();
            Date paymentDate = new Date(currentTimeMillis);
            utilitiesPaymentService.updateUtilitiesStatusToPaid(paymentId, paymentDate);

            model.addAttribute("message", "Płatność zakończona pomyślnie.");
        } else {
            model.addAttribute("error", "Brak środków w portfelu.");
        }
        return "redirect:/user_panel";
    }

    @GetMapping("/taxes_page")
    public String taxesPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.WYNAJMUJACY.getRoleName())) {
                return "redirect:/main_page";
            }

            BigDecimal walletBalance = walletService.getBalance(loggedInUser.getId());
            if (walletBalance == null) {
                walletBalance = BigDecimal.ZERO;
                session.setAttribute("walletBalance", walletBalance);
            }

            List<Property> properties = propertyService.getPropertiesForOwner(userId);

            Map<Property, BigDecimal> propertyIncomes = new HashMap<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;

            for (Property property : properties) {
                BigDecimal annualIncome = rentPaymentService.getAnnualPaymentAmount(property.getPropertyId());
                propertyIncomes.put(property, annualIncome);
                totalRevenue = totalRevenue.add(annualIncome);
            }

            List<RentalAgreement> rentalAgreements = rentalAgreementService.getRentalAgreementsForUser(userId);
            Map<RentalAgreement, BigDecimal> rentalAgreementIncomes = new HashMap<>();

            for (RentalAgreement agreement : rentalAgreements) {
                BigDecimal totalPaymentAmount = rentPaymentService.getAnnualPaymentAmount(agreement.getAgreementId());
                rentalAgreementIncomes.put(agreement, totalPaymentAmount);
            }


            BigDecimal percentageRate;

            if (totalRevenue.compareTo(new BigDecimal("100000")) <= 0) {
                percentageRate = new BigDecimal("8.5");
            } else {
                percentageRate = new BigDecimal("12.5");
            }
            model.addAttribute("propertyIncomes", propertyIncomes);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("percentageRate", percentageRate);
            model.addAttribute("rentalAgreementIncomes", rentalAgreementIncomes);


            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            model.addAttribute("walletBalance", walletBalance);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
            model.addAttribute("properties", properties);

            return "taxes_page";
    }
}
