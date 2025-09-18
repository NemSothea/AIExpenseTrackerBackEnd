package com.aiexpense.trackerbackend.service;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aiexpense.trackerbackend.config.UserPrincipal;
import com.aiexpense.trackerbackend.model.Users;
import com.aiexpense.trackerbackend.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.getUserByEmail(email);

        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

        logger.info("User found with email: {}, Role: {}", email, user.getRole());
        return new UserPrincipal(user);
    }

}
