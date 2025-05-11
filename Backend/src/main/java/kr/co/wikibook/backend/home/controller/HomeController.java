package kr.co.wikibook.backend.home.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "API Server is running.";
    }
}
