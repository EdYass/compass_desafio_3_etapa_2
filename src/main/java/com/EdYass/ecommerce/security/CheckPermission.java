package com.EdYass.ecommerce.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CheckPermission {
    public void Permission(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            for (String role : roles) {
                if (authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role))) {
                    return;
                }
            }
        }
        throw new RuntimeException("You do not have permission to perform this action");
    }
}
