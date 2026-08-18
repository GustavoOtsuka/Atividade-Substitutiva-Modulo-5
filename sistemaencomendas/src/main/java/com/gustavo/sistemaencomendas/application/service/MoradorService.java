package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.MoradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import com.gustavo.sistemaencomendas.presentation.dto.CadastroMoradorRequest;

@Service
@RequiredArgsConstructor
public class MoradorService {

    private final MoradorRepository moradorRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Morador> listarTodos() {
        return moradorRepository.findAll();
    }

    public Morador buscarPorId(Long id) {
        return moradorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Morador não encontrado."));
    }

    public Morador cadastrar(Morador morador) {
        if (moradorRepository.existsByEmail(morador.getEmail())) {
            throw new IllegalArgumentException("Já existe um morador com este e-mail.");
        }

        if (moradorRepository.existsByLogin(morador.getLogin())) {
            throw new IllegalArgumentException("Já existe um morador com este login.");
        }

        morador.setSenha(passwordEncoder.encode("123456"));
        morador.setAtivo(true);

        return moradorRepository.save(morador);
    }

    public Morador atualizar(Long id, Morador dados) {
        Morador morador = buscarPorId(id);

        moradorRepository.findAll().stream()
                .filter(outro -> !outro.getId().equals(id))
                .filter(outro -> outro.getEmail().equalsIgnoreCase(dados.getEmail()))
                .findFirst()
                .ifPresent(outro -> {
                    throw new IllegalArgumentException("Já existe um morador com este e-mail.");
                });

        moradorRepository.findAll().stream()
                .filter(outro -> !outro.getId().equals(id))
                .filter(outro -> outro.getLogin().equalsIgnoreCase(dados.getLogin()))
                .findFirst()
                .ifPresent(outro -> {
                    throw new IllegalArgumentException("Já existe um morador com este login.");
                });

        morador.setNome(dados.getNome());
        morador.setApartamento(dados.getApartamento());
        morador.setTelefone(dados.getTelefone());
        morador.setEmail(dados.getEmail());
        morador.setLogin(dados.getLogin());
        morador.setAtivo(dados.isAtivo());

        return moradorRepository.save(morador);
    }

	public Morador buscarPorLogin(String login) {
	    return moradorRepository.findByLogin(login)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("Morador não encontrado."));
	}




	public Morador atualizarDadosMorador(Long id, Morador dados) {
	    Morador morador = buscarPorId(id);

	    moradorRepository.findAll().stream()
	            .filter(outro -> !outro.getId().equals(id))
	            .filter(outro ->
	                    outro.getEmail().equalsIgnoreCase(dados.getEmail())
	            )
	            .findFirst()
	            .ifPresent(outro -> {
	                throw new IllegalArgumentException(
	                        "Já existe um morador com este e-mail."
	                );
	            });

	    morador.setNome(dados.getNome());
	    morador.setTelefone(dados.getTelefone());
	    morador.setEmail(dados.getEmail());

	    return moradorRepository.save(morador);
	}

    public Morador cadastrarPublico(CadastroMoradorRequest dados) {

        if (moradorRepository.existsByEmail(dados.email())) {
            throw new IllegalArgumentException(
                    "Já existe um morador com este e-mail."
            );
        }

        if (moradorRepository.existsByLogin(dados.login())) {
            throw new IllegalArgumentException(
                    "Já existe um morador com este login."
            );
        }

        if (dados.senha() == null || dados.senha().length() < 6) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 6 caracteres."
            );
        }

        Morador morador = Morador.builder()
                .nome(dados.nome())
                .apartamento(dados.apartamento())
                .telefone(dados.telefone())
                .email(dados.email())
                .login(dados.login())
                .senha(passwordEncoder.encode(dados.senha()))
                .ativo(true)
                .build();

        return moradorRepository.save(morador);
    }


}
