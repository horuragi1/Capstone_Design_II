package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String id,
            @RequestParam String pw,
            @RequestParam String ip) {

        System.out.println("ID: " + id);
        System.out.println("Password: " + pw);
        System.out.println("IP Address: " + ip);

        return "redirect:/screen.html";
    }

}
