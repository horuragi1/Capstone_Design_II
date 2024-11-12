package com.freerdp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String user,
            @RequestParam String pw,
            @RequestParam String ip) {

        String args = String.format("./freerdp /u:%s /p:%s /v:%s", user, pw, ip);

        logger.info("input argv: {}",args) ;

        return "redirect:/screen.html";
    }

}