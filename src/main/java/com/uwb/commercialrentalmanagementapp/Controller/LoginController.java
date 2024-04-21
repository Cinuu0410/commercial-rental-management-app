package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        if (userService.authenticate(username, password)) {
            User loggedInUser = userService.getUserByUsername(username);
            Long userId = loggedInUser.getId();

            // Pobierz rolę użytkownika na podstawie ID
            String loggedRole = userService.getRole(userId);
            System.out.println(loggedRole);
            // Zapisz informacje o zalogowanym użytkowniku i roli w sesji
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("role", loggedRole);

            redirectAttributes.addFlashAttribute("successMessage", "Zalogowano pomyślnie");
            return "redirect:/main_page";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd logowania");
            return "redirect:/login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Pobierz atrybut sesji loggedInUser
        Object loggedInUser = session.getAttribute("loggedInUser");

        // Jeżeli użytkownik jest zalogowany, wykonaj operacje wylogowywania
        if (loggedInUser != null) {
            // Tutaj możesz dodać inne operacje związane z wylogowywaniem
            // na przykład: zakończenie sesji, czyszczenie atrybutów sesji itp.

            // Usuń atrybut sesji zalogowanego użytkownika
            session.removeAttribute("loggedInUser");
        }

        // Przekieruj użytkownika na stronę główną lub inną stronę po wylogowaniu
        return "redirect:/main_page";
    }
}
