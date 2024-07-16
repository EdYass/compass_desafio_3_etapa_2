package com.EdYass.ecommerce.controller;

import com.EdYass.ecommerce.dto.JwtAuthenticationResponseDTO;
import com.EdYass.ecommerce.dto.LoginRequestDTO;
import com.EdYass.ecommerce.dto.UserDTO;
import com.EdYass.ecommerce.entity.User;
import com.EdYass.ecommerce.exception.ErrorResponse;
import com.EdYass.ecommerce.security.JwtTokenProvider;
import com.EdYass.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication.getName(), authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return ResponseEntity.ok(new JwtAuthenticationResponseDTO(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Bad Request", "User not found"));
        }
        userService.generateResetToken(email);
        return ResponseEntity.ok("Reset token sent to email");
    }


    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmResetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}