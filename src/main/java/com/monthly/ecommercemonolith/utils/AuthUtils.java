package com.monthly.ecommercemonolith.utils;

import com.monthly.ecommercemonolith.entities.User;
import com.monthly.ecommercemonolith.exceptions.ResourceNotFoundException;
import com.monthly.ecommercemonolith.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    @Autowired
    private UserRepository userRepository;

    public Long loggedInUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User user =
                userRepository.findByUsername(authentication.getName()).orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                });
        return user.getUserId();
    }

    public User loggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User user =
                userRepository.findByUsername(authentication.getName()).orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                });
        return user;
    }

    public String loggedInEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User user =
                userRepository.findByUsername(authentication.getName()).orElseThrow(() -> {
                    throw new UsernameNotFoundException("User not found");
                });
        return user.getEmail();
    }
}
