package com.gustavo.sistemaencomendas.application.service;

import com.gustavo.sistemaencomendas.domain.model.Encomenda;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.domain.model.StatusEncomenda;
import com.gustavo.sistemaencomendas.domain.model.StatusNotificacao;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.EncomendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EncomendaService {

    private final EncomendaRepository encomendaRepository;
    private final MoradorService moradorService;

    public List<Encomenda> listarTodas() {
        return encomendaRepository.findAllByOrderByDataRecebimentoDesc();
    }

    public Encomenda buscarPorId(Long id) {
        return encomendaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Encomenda não encontrada."));
    }

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
                .build();

        return encomendaRepository.save(encomenda);
    }

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
}