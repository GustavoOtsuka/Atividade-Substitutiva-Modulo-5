package com.gustavo.sistemaencomendas.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.sistemaencomendas.application.dto.EncomendaRecebidaEvento;
import com.gustavo.sistemaencomendas.domain.model.Encomenda;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.domain.model.OutboxEvento;
import com.gustavo.sistemaencomendas.domain.model.StatusEncomenda;
import com.gustavo.sistemaencomendas.domain.model.StatusNotificacao;
import com.gustavo.sistemaencomendas.domain.model.StatusOutbox;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.EncomendaRepository;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.OutboxEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final OutboxEventoRepository outboxEventoRepository;
    private final MoradorService moradorService;
    private final ObjectMapper objectMapper;

    public List<Encomenda> listarTodas() {
        return encomendaRepository.findAllByOrderByDataRecebimentoDesc();
    }

    public Encomenda buscarPorId(Long id) {
        return encomendaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Encomenda não encontrada."));
    }

    @Transactional
    public Encomenda cadastrar(Long moradorId, String descricao) {
        Morador morador = moradorService.buscarPorId(moradorId);

        if (!morador.isAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível registrar encomenda para morador inativo."
            );
        }

        Encomenda encomenda = Encomenda.builder()
                .morador(morador)
                .descricao(descricao)
                .dataRecebimento(LocalDateTime.now())
                .status(StatusEncomenda.RECEBIDA_NA_PORTARIA)
                .statusNotificacao(StatusNotificacao.PENDENTE)
                .moradorCiente(false)
		.tokenConfirmacao(UUID.randomUUID())
                .build();

        encomenda = encomendaRepository.save(encomenda);

        UUID eventoId = UUID.randomUUID();

        EncomendaRecebidaEvento evento = new EncomendaRecebidaEvento(
                eventoId,
                encomenda.getId(),
                morador.getId(),
                morador.getNome(),
                morador.getEmail(),
                morador.getApartamento(),
                encomenda.getDescricao(),
                encomenda.getDataRecebimento(),
		encomenda.getTokenConfirmacao()
        );

        OutboxEvento outboxEvento = OutboxEvento.builder()
                .id(eventoId)
                .tipoEvento("ENCOMENDA_RECEBIDA")
                .agregadoTipo("ENCOMENDA")
                .agregadoId(encomenda.getId().toString())
                .payload(converterParaJson(evento))
                .status(StatusOutbox.PENDENTE)
                .tentativas(0)
                .criadoEm(LocalDateTime.now())
                .build();

        outboxEventoRepository.save(outboxEvento);

        return encomenda;
    }

    @Transactional
    public Encomenda registrarRetirada(Long id) {
        Encomenda encomenda = buscarPorId(id);

        if (encomenda.getStatus() == StatusEncomenda.ENTREGUE_AO_MORADOR) {
            throw new IllegalArgumentException(
                    "Esta encomenda já foi entregue ao morador."
            );
        }

        encomenda.setStatus(StatusEncomenda.ENTREGUE_AO_MORADOR);
        encomenda.setDataRetirada(LocalDateTime.now());

        return encomendaRepository.save(encomenda);
    }


    @Transactional
    public void confirmarCiencia(UUID tokenConfirmacao) {

        Encomenda encomenda = encomendaRepository
                .findByTokenConfirmacao(tokenConfirmacao)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Token de confirmação inválido."
                        )
                );

	if (encomenda.isMoradorCiente()) {
	  return;
	}

        encomenda.setMoradorCiente(true);
        encomenda.setDataCiencia(LocalDateTime.now());

        encomendaRepository.save(encomenda);
    }

    private String converterParaJson(EncomendaRecebidaEvento evento) {
        try {
            return objectMapper.writeValueAsString(evento);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Não foi possível criar o evento da encomenda.",
                    exception
            );
        }
    }
}
