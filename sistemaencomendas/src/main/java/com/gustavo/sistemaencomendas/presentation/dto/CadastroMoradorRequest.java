package com.gustavo.sistemaencomendas.presentation.dto;

public record CadastroMoradorRequest(
        String nome,
        String apartamento,
        String telefone,
        String email,
        String login,
        String senha
) {
}