package com.gustavo.sistemaencomendas.presentation.dto;

public record CadastroFuncionarioRequest(
        String nome,
        String email,
        String login,
        String senha
) {
}