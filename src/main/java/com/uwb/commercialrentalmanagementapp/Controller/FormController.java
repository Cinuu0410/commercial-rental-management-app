package com.uwb.commercialrentalmanagementapp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FormController {

    @PostMapping("/submitForm")
    public String submitForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("sub") String subject,
            @RequestParam("message") String message,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Imię nie może zawierać cyfr
        if (containsDigit(name)) {
            redirectAttributes.addFlashAttribute("error", "Imię nie może zawierać cyfr. Wprowadź odpowiednie dane jeszcze raz.");
            return "redirect:/contact_us";
        }

        // Mumer telefonu musi zawierać dokładnie 9 cyfr
        if (!isValidPhoneNumber(phone)) {
            redirectAttributes.addFlashAttribute("error", "Numer telefonu musi mieć dokładnie 9 cyfr. Wprowadź odpowiednie dane jeszcze raz.");
            return "redirect:/contact_us";
        }

        // Walidacja pola email przy użyciu  regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            redirectAttributes.addFlashAttribute("error", "Niepoprawny adres email. Wprowadź odpowiednie dane jeszcze raz.");
            return "redirect:/contact_us"; // Przekierowanie do strony błędu
        }

        // Procesowanie danych formularza
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("subject", subject);
        model.addAttribute("message", message);

        return "confirmation_page"; // Przekierowanie do strony potwierdzenia
    }

    // Metoda pomocnicza sprawdzająca, czy w ciągu znaków występuje cyfra
    private boolean containsDigit(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    // Metoda pomocnicza sprawdzająca poprawność numeru telefonu
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Sprawdzenie, czy numer telefonu ma dokładnie 9 cyfr
        return phoneNumber.matches("\\d{9}");
    }
}
