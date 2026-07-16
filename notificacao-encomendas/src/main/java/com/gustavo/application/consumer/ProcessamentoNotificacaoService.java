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

    private static final int LIMITE_TENTATIVAS = 3;

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
        notificacao.apartamento = evento.apartamento();
        notificacao.descricao = evento.descricao();
        notificacao.dataRecebimento = evento.dataRecebimento();
        notificacao.status = StatusProcessamento.RECEBIDA;
        notificacao.tentativas = 0;
        notificacao.recebidaEm = LocalDateTime.now();

        repository.persist(notificacao);

        return true;
    }

    @Transactional
    public void marcarComoProcessando(EncomendaRecebidaEvento evento) {

        NotificacaoProcessada notificacao =
                buscarNotificacao(evento);

        notificacao.status = StatusProcessamento.PROCESSANDO;
        notificacao.tentativas++;
    }

    @Transactional
    public void marcarComoEnviada(EncomendaRecebidaEvento evento) {

        NotificacaoProcessada notificacao =
                buscarNotificacao(evento);

        notificacao.status = StatusProcessamento.ENVIADA;
        notificacao.processadaEm = LocalDateTime.now();
        notificacao.proximaTentativaEm = null;
        notificacao.ultimoErro = null;
    }

    @Transactional
    public void marcarComoFalhaTemporaria(
            EncomendaRecebidaEvento evento,
            Exception exception
    ) {
        NotificacaoProcessada notificacao =
                buscarNotificacao(evento);

        notificacao.ultimoErro = resumirErro(exception);

        if (notificacao.tentativas >= LIMITE_TENTATIVAS) {
            notificacao.status =
                    StatusProcessamento.FALHA_DEFINITIVA;

            notificacao.proximaTentativaEm = null;
            notificacao.processadaEm = LocalDateTime.now();

            return;
        }

        notificacao.status =
                StatusProcessamento.FALHA_TEMPORARIA;

        notificacao.proximaTentativaEm =
                LocalDateTime.now().plusSeconds(30);
    }

    private NotificacaoProcessada buscarNotificacao(
            EncomendaRecebidaEvento evento
    ) {
        return repository.findByIdOptional(evento.eventoId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Notificação não encontrada."
                        )
                );
    }

    private String resumirErro(Exception exception) {

        String mensagem = exception.getMessage();

        if (mensagem == null || mensagem.isBlank()) {
            mensagem = exception.getClass().getSimpleName();
        }

        return mensagem.length() > 1000
                ? mensagem.substring(0, 1000)
                : mensagem;
    }
}
