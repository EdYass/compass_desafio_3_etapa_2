package com.EdYass.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtAuthenticationResponseDTO {

    private String accessToken;

    public JwtAuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

}
