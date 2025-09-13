package com.aiexpense.TrackerBackEnd.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public String greet(HttpServletRequest request) {
        return new String("Welcome to AI Expense Tracker:" + request.getSession().getId());
    }

}
