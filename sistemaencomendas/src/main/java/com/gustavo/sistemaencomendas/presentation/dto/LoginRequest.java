package com.gustavo.sistemaencomendas.presentation.dto;

public record LoginRequest(
        String login,
        String senha
) {
}