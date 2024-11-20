package com.freerdp.controller;

import com.freerdp.services.LibFreeRDP;
import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String user,
            @RequestParam String pw,
            @RequestParam String ip) {

        String args = String.format("./freerdp /u:%s /p:%s /v:%s", user, pw, ip);

        logger.info("input args: {}", args);
        UserData.instance = LibFreeRDP.newInstance();

        if(!LibFreeRDP.login(UserData.instance, args))
            logger.info("Login Fail");
        else
            logger.info("Login Success");

        if(!LibFreeRDP.connect(UserData.instance))
            logger.info("Connect Fail");
        else
            logger.info("Connect Success");

        return "redirect:/screen.html";
    }

}