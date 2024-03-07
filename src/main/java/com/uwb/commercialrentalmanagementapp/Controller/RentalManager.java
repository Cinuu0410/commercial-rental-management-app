package com.uwb.commercialrentalmanagementapp.Controller;


import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Slf4j
public class RentalManager {
    private final UserService userService;

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

    public String panelPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = Long.valueOf(loggedInUser.getId());
            String loggedRole = userService.getRole(userId);

            if (!loggedRole.equals(UserRole.SUPER_USER.getRoleName()) ) {
                // Użytkownik nie ma roli SUPER_USER, przekieruj na stronę główną
                return "redirect:/main_page";
            }
            // Zapisz informacje o zalogowanym użytkowniku i roli w sesji
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);
            // Dodaj informacje o zalogowanym użytkowniku do modelu
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("role", loggedRole);

        }
        else {
            return "redirect:/login";
        }

        return "main_page";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login_page";
    }


}
