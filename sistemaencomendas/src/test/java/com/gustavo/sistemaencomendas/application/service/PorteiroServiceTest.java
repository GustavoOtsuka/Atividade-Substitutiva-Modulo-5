package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.PorteiroRepository;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroFuncionarioRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PorteiroServiceTest {

    @Mock
    private PorteiroRepository porteiroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PorteiroService porteiroService;

    @Test
    void deveCadastrarFuncionarioComSenhaCriptografadaEAtivo() {

        CadastroFuncionarioRequest dados =
                new CadastroFuncionarioRequest(
                        "Carlos Porteiro",
                        "carlos@email.com",
                        "carlos",
                        "senha123"
                );

        when(porteiroRepository.existsByEmail("carlos@email.com"))
                .thenReturn(false);

        when(porteiroRepository.existsByLogin("carlos"))
                .thenReturn(false);

        when(passwordEncoder.encode("senha123"))
                .thenReturn("senha-criptografada");

        when(porteiroRepository.save(any(Porteiro.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Porteiro salvo = porteiroService.cadastrarPublico(dados);

        assertNotNull(salvo);
        assertEquals("Carlos Porteiro", salvo.getNome());
        assertEquals("carlos@email.com", salvo.getEmail());
        assertEquals("carlos", salvo.getLogin());
        assertEquals("senha-criptografada", salvo.getSenha());
        assertTrue(salvo.isAtivo());

        verify(passwordEncoder).encode("senha123");
        verify(porteiroRepository).save(any(Porteiro.class));
    }

    @Test
    void naoDeveCadastrarFuncionarioComEmailDuplicado() {

        CadastroFuncionarioRequest dados =
                new CadastroFuncionarioRequest(
                        "Carlos Porteiro",
                        "carlos@email.com",
                        "carlos",
                        "senha123"
                );

        when(porteiroRepository.existsByEmail("carlos@email.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> porteiroService.cadastrarPublico(dados)
        );

        assertEquals(
                "Já existe um funcionário com este e-mail.",
                exception.getMessage()
        );

        verify(porteiroRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void naoDeveCadastrarFuncionarioComLoginDuplicado() {

        CadastroFuncionarioRequest dados =
                new CadastroFuncionarioRequest(
                        "Carlos Porteiro",
                        "carlos@email.com",
                        "carlos",
                        "senha123"
                );

        when(porteiroRepository.existsByEmail("carlos@email.com"))
                .thenReturn(false);

        when(porteiroRepository.existsByLogin("carlos"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> porteiroService.cadastrarPublico(dados)
        );

        assertEquals(
                "Já existe um funcionário com este login.",
                exception.getMessage()
        );

        verify(porteiroRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void naoDeveCadastrarFuncionarioComSenhaMenorQueSeisCaracteres() {

        CadastroFuncionarioRequest dados =
                new CadastroFuncionarioRequest(
                        "Carlos Porteiro",
                        "carlos@email.com",
                        "carlos",
                        "12345"
                );

        when(porteiroRepository.existsByEmail("carlos@email.com"))
                .thenReturn(false);

        when(porteiroRepository.existsByLogin("carlos"))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> porteiroService.cadastrarPublico(dados)
        );

        assertEquals(
                "A senha deve possuir pelo menos 6 caracteres.",
                exception.getMessage()
        );

        verify(porteiroRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}