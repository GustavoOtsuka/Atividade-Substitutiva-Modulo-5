package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.PorteiroRepository;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroFuncionarioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PorteiroService {

    private final PorteiroRepository porteiroRepository;
    private final PasswordEncoder passwordEncoder;

    public Porteiro cadastrarPublico(CadastroFuncionarioRequest dados) {

        if (porteiroRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException(
                    "Já existe um funcionário com este e-mail."
            );
        }

        if (porteiroRepository.existsByLogin(dados.login())) {
            throw new IllegalArgumentException(
                    "Já existe um funcionário com este login."
            );
        }

        if (dados.senha() == null || dados.senha().length() < 6) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 6 caracteres."
            );
        }

        Porteiro porteiro = Porteiro.builder()
                .nome(dados.nome())
                .email(dados.email())
                .login(dados.login())
                .senha(passwordEncoder.encode(dados.senha()))
                .ativo(true)
                .build();

        return porteiroRepository.save(porteiro);
    }
}