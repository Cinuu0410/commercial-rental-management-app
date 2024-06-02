package com.uwb.commercialrentalmanagementapp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormController {

    @PostMapping("/submitForm")
    public String submitForm(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("sub") String subject,
            @RequestParam("message") String message,
            Model model) {

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("subject", subject);
        model.addAttribute("message", message);

        return "confirmation_page";
    }
}
