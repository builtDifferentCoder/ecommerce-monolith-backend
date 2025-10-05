package com.monthly.ecommercemonolith.controllers;

import com.monthly.ecommercemonolith.entities.AppRole;
import com.monthly.ecommercemonolith.entities.Role;
import com.monthly.ecommercemonolith.entities.User;
import com.monthly.ecommercemonolith.repositories.RoleRepository;
import com.monthly.ecommercemonolith.repositories.UserRepository;
import com.monthly.ecommercemonolith.security.jwt.JWTUtils;
import com.monthly.ecommercemonolith.security.request.LoginRequest;
import com.monthly.ecommercemonolith.security.request.SignUpRequest;
import com.monthly.ecommercemonolith.security.response.MessageResponse;
import com.monthly.ecommercemonolith.security.response.UserInfoResponse;
import com.monthly.ecommercemonolith.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJWTCookie(userDetails);
        List<String> roles =
                userDetails.getAuthorities().stream().map(i -> i.getAuthority()).toList();
        UserInfoResponse userInfoResponse =
                new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(), roles);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                    "Error: username is already taken"));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse(
                    "Error: email is already taken"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(), passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role role =
                    roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() ->
                            new RuntimeException("Error: role is not found")
                    );
            roles.add(role);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole =
                                roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() ->
                                        new RuntimeException("Error: role is not found")
                                );
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole =
                                roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() ->
                                        new RuntimeException("Error: role is not found")
                                );
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole =
                                roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() ->
                                        new RuntimeException("Error: role is not found")
                                );
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered " +
                "successfully"));

    }

    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if (authentication != null) {
            return authentication.getName();
        } else return null;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles =
                userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        UserInfoResponse userInfoResponse =
                new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(userInfoResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                cookie.toString()).body(new MessageResponse("You have been " +
                "signed out"));
    }
}
