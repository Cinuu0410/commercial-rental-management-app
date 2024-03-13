package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin_panel")
    public String adminPanel(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Pobierz informacje o zalogowanym użytkowniku
            Long userId = Long.valueOf(loggedInUser.getId());
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
            Long userId = Long.valueOf(loggedInUser.getId());
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
}
