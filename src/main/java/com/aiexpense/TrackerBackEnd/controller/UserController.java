package com.aiexpense.trackerbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aiexpense.trackerbackend.model.Users;
import com.aiexpense.trackerbackend.service.UserService;

@RestController

public class UserController {

    @Autowired
    private UserService service;
    // Get all categories

    @GetMapping("/api/users")
    public List<Users> getAllUsers() {
        return service.getAllUsers();
    }
    // Get user by ID
    @GetMapping("/api/user/{id}")
    public Optional<Users> getUserById(@PathVariable Integer id) {
        return service.getUserById(id);
    }

    @PostMapping("/api/user/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);
    }

    @PostMapping("/api/user/login")
    public String login(@RequestBody Users user) {
        System.out.println(user);
        // return "Success";
        return service.vertify(user);

    }

}
