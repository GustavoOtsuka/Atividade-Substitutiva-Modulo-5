package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.MoradorRepository;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroMoradorRequest;
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

    @Test
    void naoDeveCadastrarMoradorComLoginDuplicado() {
        when(moradorRepository.existsByEmail("joao@email.com"))
                .thenReturn(false);

        when(moradorRepository.existsByLogin("joao"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.cadastrar(morador)
        );

        assertEquals(
                "Já existe um morador com este login.",
                exception.getMessage()
        );

        verify(moradorRepository, never()).save(any());
    }

    @Test
    void deveCadastrarMoradorPublicoComSenhaEscolhidaCriptografada() {

        CadastroMoradorRequest dados = new CadastroMoradorRequest(
                "Maria Oliveira",
                "202",
                "11988887777",
                "maria@email.com",
                "maria",
                "senha123"
        );

        when(moradorRepository.existsByEmail("maria@email.com"))
                .thenReturn(false);

        when(moradorRepository.existsByLogin("maria"))
                .thenReturn(false);

        when(passwordEncoder.encode("senha123"))
                .thenReturn("senha-publica-criptografada");

        when(moradorRepository.save(any(Morador.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Morador salvo = moradorService.cadastrarPublico(dados);

        assertNotNull(salvo);
        assertEquals("Maria Oliveira", salvo.getNome());
        assertEquals("202", salvo.getApartamento());
        assertEquals("11988887777", salvo.getTelefone());
        assertEquals("maria@email.com", salvo.getEmail());
        assertEquals("maria", salvo.getLogin());
        assertEquals(
                "senha-publica-criptografada",
                salvo.getSenha()
        );
        assertTrue(salvo.isAtivo());

        verify(passwordEncoder).encode("senha123");
        verify(moradorRepository).save(any(Morador.class));
    }

    @Test
    void naoDeveCadastrarMoradorPublicoComEmailDuplicado() {

        CadastroMoradorRequest dados = new CadastroMoradorRequest(
                "Maria Oliveira",
                "202",
                "11988887777",
                "maria@email.com",
                "maria",
                "senha123"
        );

        when(moradorRepository.existsByEmail("maria@email.com"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.cadastrarPublico(dados)
        );

        assertEquals(
                "Já existe um morador com este e-mail.",
                exception.getMessage()
        );

        verify(moradorRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void naoDeveCadastrarMoradorPublicoComLoginDuplicado() {

        CadastroMoradorRequest dados = new CadastroMoradorRequest(
                "Maria Oliveira",
                "202",
                "11988887777",
                "maria@email.com",
                "maria",
                "senha123"
        );

        when(moradorRepository.existsByEmail("maria@email.com"))
                .thenReturn(false);

        when(moradorRepository.existsByLogin("maria"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.cadastrarPublico(dados)
        );

        assertEquals(
                "Já existe um morador com este login.",
                exception.getMessage()
        );

        verify(moradorRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void naoDeveCadastrarMoradorPublicoComSenhaMenorQueSeisCaracteres() {

        CadastroMoradorRequest dados = new CadastroMoradorRequest(
                "Maria Oliveira",
                "202",
                "11988887777",
                "maria@email.com",
                "maria",
                "12345"
        );

        when(moradorRepository.existsByEmail("maria@email.com"))
                .thenReturn(false);

        when(moradorRepository.existsByLogin("maria"))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.cadastrarPublico(dados)
        );

        assertEquals(
                "A senha deve possuir pelo menos 6 caracteres.",
                exception.getMessage()
        );

        verify(moradorRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveBuscarMoradorPorId() {

        morador.setId(1L);

        when(moradorRepository.findById(1L))
                .thenReturn(java.util.Optional.of(morador));

        Morador encontrado = moradorService.buscarPorId(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        assertEquals("João da Silva", encontrado.getNome());
    }

    @Test
    void deveFalharAoBuscarMoradorInexistente() {

        when(moradorRepository.findById(99L))
                .thenReturn(java.util.Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> moradorService.buscarPorId(99L)
        );

        assertEquals(
                "Morador não encontrado.",
                exception.getMessage()
        );
    }
}