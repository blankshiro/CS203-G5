package com.cs203t5.ryverbank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LandingController {
    @GetMapping("/")docker
    public String welcome(){
        return "Welcome to Ryverbank - Connection successful";
    }

}
