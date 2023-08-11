package com.cointr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/main")
    public String main(Model model) {
        return "main";
    }
    @GetMapping("/index")
    public String index(Model model) {
        return "index";
    }
    @GetMapping("/trade")
    public String trade(Model model) {
        return "trade";
    }
}
