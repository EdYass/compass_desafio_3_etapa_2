package com.EdYass.ecommerce.dto;

public class JwtAuthenticationResponseDTO {

    private String accessToken;

    public JwtAuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
