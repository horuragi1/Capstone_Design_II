package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String user,
            @RequestParam String pw,
            @RequestParam String ip) {

        String argv = String.format("./freerdp /u:%s /p:%s /v:%s", user, pw, ip);

        System.out.println(argv);

        return "redirect:/screen.html";
    }

}
