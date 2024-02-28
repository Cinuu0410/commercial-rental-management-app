package com.uwb.commercialrentalmanagementapp.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class RentalManager {

    @GetMapping("/main_page")
    public String mainPage(){
        return "main_page";
    }



}
