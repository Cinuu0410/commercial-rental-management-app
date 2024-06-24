package com.uwb.commercialrentalmanagementapp.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uwb.commercialrentalmanagementapp.Model.*;
import com.uwb.commercialrentalmanagementapp.Repository.PropertyRepository;
import com.uwb.commercialrentalmanagementapp.Repository.RentalAgreementRepository;
import com.uwb.commercialrentalmanagementapp.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private RentalAgreementRepository rentalAgreementRepository;

    public RentalManager(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/main_page")
    public String mainPage(Model model, HttpSession session, RedirectAttributes redirectAttributes){
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);
            System.out.println(loggedRole);
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
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
            Long userId = loggedInUser.getId();
            String loggedRole = userService.getRole(userId);
            System.out.println("Zalogowany:" + loggedRole);
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);
        }
        return "contact_us_page";
    }

    @GetMapping("/suggestions_and_analyses")
    public String suggestionsAndAnalysesPage(Model model, HttpSession session) throws JsonProcessingException {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        Long userId = loggedInUser.getId();
        String loggedRole = userService.getRole(userId);

        List<Property> properties = propertyService.getPropertiesForOwner(userId);
        List<TypeOfProperty> typesOfProperties = propertyService.getAllTypesOfProperties();
        List<Tenant> tenants = tenantService.getAllTenants();


        List<Map<String, Object>> propertyData = properties.stream().map(property -> {
            Map<String, Object> map = new HashMap<>();
            map.put("address", property.getAddress());
            BigDecimal totalIncome = rentPaymentService.getTotalRevenue(property.getPropertyId());
            map.put("income", totalIncome);
            return map;
        }).collect(Collectors.toList());

        Map<String, Map<Integer, BigDecimal>> annualIncomes = new HashMap<>();
        for (Property property : properties) {
            Map<Integer, BigDecimal> yearlyIncomes = rentPaymentService.getAnnualRevenues(property.getPropertyId());
            annualIncomes.put(property.getAddress(), yearlyIncomes);
            System.out.println(annualIncomes);
        }

        Map<String, RentPayment> lastRentInfo = new HashMap<>();
        for (Property property : properties) {
            List<RentPayment> rentPayments = rentPaymentService.getLastRentInfoForProperty(property.getPropertyId());
            if (!rentPayments.isEmpty()) {
                RentPayment lastRentPayment = rentPayments.get(0);
                lastRentInfo.put(property.getAddress(), lastRentPayment);
            }
        }
        System.out.println("Last rent Info: " + lastRentInfo);


        try {
            String propertyDataJson = new ObjectMapper().writeValueAsString(propertyData);
            String annualIncomesJson = new ObjectMapper().writeValueAsString(annualIncomes);
            String lastRentInfoJson = new ObjectMapper().writeValueAsString(lastRentInfo);
            System.out.println("Serialized annualIncomesJson: " + annualIncomesJson);
            model.addAttribute("propertyDataJson", propertyDataJson);
            model.addAttribute("annualIncomesJson", annualIncomesJson);
            model.addAttribute("lastRentInfosJson", lastRentInfoJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            model.addAttribute("jsonError", "Error processing JSON");
        }


        session.setAttribute("loggedInUser", loggedInUser);
        session.setAttribute("role", loggedRole);

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("role", loggedRole);
        model.addAttribute("properties", properties);
        model.addAttribute("typesOfProperties", typesOfProperties);
        model.addAttribute("tenants", tenants);
        return "suggestions_and_analyses_page";
    }
}
