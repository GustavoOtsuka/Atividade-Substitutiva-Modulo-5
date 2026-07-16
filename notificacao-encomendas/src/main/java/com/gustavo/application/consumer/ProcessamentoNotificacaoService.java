package com.gustavo.application.consumer;

import com.gustavo.domain.model.EncomendaRecebidaEvento;
import com.gustavo.domain.model.NotificacaoProcessada;
import com.gustavo.domain.model.StatusProcessamento;
import com.gustavo.infrastructure.persistence.NotificacaoProcessadaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ProcessamentoNotificacaoService {

    private final NotificacaoProcessadaRepository repository;

    public ProcessamentoNotificacaoService(
            NotificacaoProcessadaRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional
    public boolean registrarSeNovo(EncomendaRecebidaEvento evento) {

        if (repository.findByIdOptional(evento.eventoId()).isPresent()) {
            return false;
        }

        NotificacaoProcessada notificacao = new NotificacaoProcessada();

        notificacao.eventoId = evento.eventoId();
        notificacao.encomendaId = evento.encomendaId();
        notificacao.moradorId = evento.moradorId();
        notificacao.nomeMorador = evento.nomeMorador();
        notificacao.email = evento.emailMorador();
        notificacao.status = StatusProcessamento.RECEBIDA;
        notificacao.tentativas = 0;
        notificacao.recebidaEm = LocalDateTime.now();

        repository.persist(notificacao);

        return true;
    }
}
