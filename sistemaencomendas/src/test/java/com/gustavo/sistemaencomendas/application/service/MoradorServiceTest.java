package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.MoradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoradorServiceTest {

    @Mock
    private MoradorRepository moradorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MoradorService moradorService;

    private Morador morador;

    @BeforeEach
    void preparar() {
        morador = Morador.builder()
                .nome("João da Silva")
                .apartamento("101")
                .telefone("11999999999")
                .email("joao@email.com")
                .login("joao")
                .build();
    }

    @Test
    void deveCadastrarMoradorComSenhaCriptografadaEAtivo() {
        when(moradorRepository.existsByEmail("joao@email.com"))
                .thenReturn(false);

        when(moradorRepository.existsByLogin("joao"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("senha-criptografada");

        when(moradorRepository.save(any(Morador.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Morador salvo = moradorService.cadastrar(morador);

        assertNotNull(salvo);
        assertEquals("senha-criptografada", salvo.getSenha());
        assertTrue(salvo.isAtivo());

        verify(moradorRepository).save(morador);
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void naoDeveCadastrarMoradorComEmailDuplicado() {
        when(moradorRepository.existsByEmail("joao@email.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.cadastrar(morador)
        );

        assertEquals(
                "Já existe um morador com este e-mail.",
                exception.getMessage()
        );

        verify(moradorRepository, never()).save(any());
    }
}
