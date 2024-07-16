package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password cannot be blank") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password cannot be blank") String password) {
        this.password = password;
    }
}