package com.EdYass.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtAuthenticationResponseDTO {

    @NotBlank(message = "Email cannot be blank")
    private String accessToken;

    public JwtAuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

}
