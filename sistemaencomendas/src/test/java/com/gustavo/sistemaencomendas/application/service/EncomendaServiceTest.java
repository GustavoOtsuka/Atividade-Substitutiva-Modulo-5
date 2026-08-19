package com.gustavo.sistemaencomendas.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.sistemaencomendas.domain.model.Encomenda;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.domain.model.StatusEncomenda;
import com.gustavo.sistemaencomendas.domain.model.StatusNotificacao;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.EncomendaRepository;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.OutboxEventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncomendaServiceTest {

    @Mock
    private EncomendaRepository encomendaRepository;

    @Mock
    private OutboxEventoRepository outboxEventoRepository;

    @Mock
    private MoradorService moradorService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EncomendaService encomendaService;

    private Morador morador;

    @BeforeEach
    void preparar() {
        morador = Morador.builder()
                .id(1L)
                .nome("João da Silva")
                .apartamento("101")
                .telefone("11999999999")
                .email("joao@email.com")
                .login("joao")
                .senha("senha")
                .ativo(true)
                .build();
    }

    @Test
    void naoDeveCadastrarEncomendaParaMoradorInativo() {

        morador.setAtivo(false);

        when(moradorService.buscarPorId(1L))
                .thenReturn(morador);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> encomendaService.cadastrar(
                        1L,
                        "Caixa pequena"
                )
        );

        assertEquals(
                "Não é possível registrar encomenda para morador inativo.",
                exception.getMessage()
        );

        verify(encomendaRepository, never()).save(any());
        verify(outboxEventoRepository, never()).save(any());
    }

    @Test
    void deveBuscarEncomendaPorId() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        Encomenda encontrada =
                encomendaService.buscarPorId(10L);

        assertNotNull(encontrada);
        assertEquals(10L, encontrada.getId());
        assertEquals("Pacote", encontrada.getDescricao());
    }

    @Test
    void deveFalharAoBuscarEncomendaInexistente() {

        when(encomendaRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> encomendaService.buscarPorId(99L)
        );

        assertEquals(
                "Encomenda não encontrada.",
                exception.getMessage()
        );
    }

    @Test
    void deveRegistrarRetiradaDaEncomenda() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        when(encomendaRepository.save(any(Encomenda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Encomenda atualizada =
                encomendaService.registrarRetirada(10L);

        assertEquals(
                StatusEncomenda.ENTREGUE_AO_MORADOR,
                atualizada.getStatus()
        );

        assertNotNull(atualizada.getDataRetirada());

        verify(encomendaRepository).save(encomenda);
    }

    @Test
    void naoDeveRegistrarRetiradaDuasVezes() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.ENTREGUE_AO_MORADOR)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> encomendaService.registrarRetirada(10L)
        );

        assertEquals(
                "Esta encomenda já foi entregue ao morador.",
                exception.getMessage()
        );

        verify(encomendaRepository, never()).save(any());
    }

    @Test
    void deveConfirmarCienciaDaPropriaEncomenda() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        encomendaService.confirmarCienciaPorMorador(
                10L,
                1L
        );

        assertTrue(encomenda.isMoradorCiente());
        assertNotNull(encomenda.getDataCiencia());

        verify(encomendaRepository).save(encomenda);
    }

    @Test
    void naoDeveConfirmarEncomendaDeOutroMorador() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> encomendaService.confirmarCienciaPorMorador(
                        10L,
                        999L
                )
        );

        assertEquals(
                "Esta encomenda não pertence ao morador autenticado.",
                exception.getMessage()
        );

        assertFalse(encomenda.isMoradorCiente());

        verify(encomendaRepository, never()).save(any());
    }

    @Test
    void confirmarCienciaNovamenteDeveSerIdempotente() {

        Encomenda encomenda = Encomenda.builder()
                .id(10L)
                .morador(morador)
                .descricao("Pacote")
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(true)
                .build();

        when(encomendaRepository.findById(10L))
                .thenReturn(Optional.of(encomenda));

        encomendaService.confirmarCienciaPorMorador(
                10L,
                1L
        );

        verify(encomendaRepository, never()).save(any());
    }
}