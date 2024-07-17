package com.EdYass.ecommerce.controller;

import com.EdYass.ecommerce.dto.JwtAuthenticationResponseDTO;
import com.EdYass.ecommerce.dto.LoginRequestDTO;
import com.EdYass.ecommerce.dto.ResetPasswordRequestDTO;
import com.EdYass.ecommerce.dto.UserDTO;
import com.EdYass.ecommerce.entity.User;
import com.EdYass.ecommerce.exception.error.ErrorResponse;
import com.EdYass.ecommerce.exception.UserNotFoundException;
import com.EdYass.ecommerce.exception.error.ErrorResponseBuilder;
import com.EdYass.ecommerce.service.AuthService;
import com.EdYass.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            String jwt = authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            return ResponseEntity.ok(new JwtAuthenticationResponseDTO(jwt));
        } catch (UserNotFoundException ex) {
            return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (AuthenticationException ex) {
            return buildErrorResponse("Invalid password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.findByEmail(email);
        userService.generateResetToken(email);
        return ResponseEntity.ok("Reset token sent to email");
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> confirmResetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        userService.resetPassword(resetPasswordRequestDTO.getToken(), resetPasswordRequestDTO.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        return ErrorResponseBuilder.buildErrorResponse(message, status);
    }
}