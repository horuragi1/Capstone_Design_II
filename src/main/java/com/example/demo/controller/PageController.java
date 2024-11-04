package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/screen")
    public String screenPage() {
        return "redirect:/screen.html";
    }
}
