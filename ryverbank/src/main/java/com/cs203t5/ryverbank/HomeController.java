package com.cs203t5.ryverbank;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;


//The @RestController component tells Spring that this class has methods that map to URL requests
//Use @Controller to use .HTML format - RestController does not work as it renders JSON, not html
// @RestController
@Controller
public class HomeController {
    private final String ryberBankVision = "For our Ryver Bank employees\n we also have a plan for the Ryver Bank. \nWe have a Ryver Bank, we have a together and Ryver Bank plan. \nWe care at the Ryver Bank";
    /*
        @RequestMapping(<URL to map to>) - This tells Spring that I want this method to be executed when this URL is visited
        @RequestMapping maps to only the GET method, need to specify if mapping to other HTTP methods
    */
    //This will represent the root of the webpage; e.g. Ryverbank.com.sg 
    //The return statement returns the VIEW NAME - i.e. What HTML stuff you have
    @GetMapping("/")
    public String home (){ 
        return "home";
    }

    @GetMapping("/login")
    public String sayWelcome (){ 
        return "Please enter your username and password to enter";
    }



}
