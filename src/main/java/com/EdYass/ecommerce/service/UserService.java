package com.EdYass.ecommerce.service;

import com.EdYass.ecommerce.dto.UserDTO;
import com.EdYass.ecommerce.entity.User;
import com.EdYass.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setEmail(user.getEmail());
                    userDTO.setRoles(new ArrayList<>(user.getRoles()));
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setRoles(new ArrayList<>(user.getRoles()));
        return userDTO;
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(new HashSet<>(userDTO.getRoles()));
        userRepository.save(user);

        return userDTO;
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setRoles(new HashSet<>(userDetails.getRoles()));
        userRepository.save(user);

        return userDetails;
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Transactional
    public void generateResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String subject = "Password Reset Request";
        String text = "To reset your password, please use the following token: " + token;
        emailService.sendSimpleMessage(email, subject, text);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            throw new RuntimeException("Invalid token");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
