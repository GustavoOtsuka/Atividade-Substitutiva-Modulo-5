package com.gustavo.sistemaencomendas.presentation.dto;

public record LoginResponse(
        String token,
        String tipo,
        String login,
        String role
) {
}