package com.aiexpense.trackerbackend.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class WelcomeController {
    
    @GetMapping("/welcome")
    public String greet(HttpServletRequest request) {
        return new String("Welcome to AI Expense Tracker" );//+ request.getSession().getId()
    }
    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        return new String("Welcome to AI Expense Tracker" );//+ request.getSession().getId()
    }

}
