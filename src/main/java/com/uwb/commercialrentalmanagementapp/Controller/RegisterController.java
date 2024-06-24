package com.uwb.commercialrentalmanagementapp.Controller;

import com.uwb.commercialrentalmanagementapp.Enum.UserRole;
import com.uwb.commercialrentalmanagementapp.Service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "register_page";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam String firstName, @RequestParam String lastName,
                           @RequestParam String email, @RequestParam UserRole role,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {

        if (username.length() < 5 || !username.matches("[a-zA-Z0-9]+")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nazwa użytkownika musi mieć co najmniej 5 znaków i może składać się tylko z liter i cyfr");
            return "redirect:/register";
        }

        if (password.length() < 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hasło musi mieć co najmniej 5 znaków");
            return "redirect:/register";
        }

        if (!firstName.matches("[a-zA-Z]+")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Imię może zawierać tylko litery");
            return "redirect:/register";
        }

        if (!lastName.matches("[a-zA-Z]+")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nazwisko może zawierać tylko litery");
            return "redirect:/register";
        }

        if (userService.userExists(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Użytkownik o podanej nazwie już istnieje");
            return "redirect:/register";
        }

        userService.register(username, password, firstName, lastName, email, role);

        session.setAttribute("username", username);

        redirectAttributes.addFlashAttribute("successMessage", "Rejestracja udana!");
        return "redirect:/main_page";
    }
}
