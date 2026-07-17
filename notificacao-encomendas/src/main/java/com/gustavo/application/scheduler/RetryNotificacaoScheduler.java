package com.gustavo.application.scheduler;

import com.gustavo.application.consumer.ProcessamentoNotificacaoService;
import com.gustavo.domain.model.EncomendaRecebidaEvento;
import com.gustavo.domain.model.NotificacaoProcessada;
import com.gustavo.infrastructure.email.NotificacaoEmailService;
import com.gustavo.infrastructure.persistence.NotificacaoProcessadaRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class RetryNotificacaoScheduler {

    private static final Logger LOG =
            Logger.getLogger(RetryNotificacaoScheduler.class);

    private static final int LIMITE_TENTATIVAS = 3;

    private final NotificacaoProcessadaRepository repository;
    private final ProcessamentoNotificacaoService processamentoService;
    private final NotificacaoEmailService emailService;

    public RetryNotificacaoScheduler(
            NotificacaoProcessadaRepository repository,
            ProcessamentoNotificacaoService processamentoService,
            NotificacaoEmailService emailService
    ) {
        this.repository = repository;
        this.processamentoService = processamentoService;
        this.emailService = emailService;
    }

    @Scheduled(every = "10s")
    void executarRetry() {

        List<NotificacaoProcessada> notificacoes =
                repository.buscarProntasParaRetry(
                        LocalDateTime.now(),
                        LIMITE_TENTATIVAS
                );

        for (NotificacaoProcessada notificacao : notificacoes) {
            tentarNovamente(notificacao);
        }
    }

    private void tentarNovamente(NotificacaoProcessada notificacao) {

        EncomendaRecebidaEvento evento =
                new EncomendaRecebidaEvento(
                        notificacao.eventoId,
                        notificacao.encomendaId,
                        notificacao.moradorId,
                        notificacao.nomeMorador,
                        notificacao.email,
                        notificacao.apartamento,
                        notificacao.descricao,
                        notificacao.dataRecebimento,
			notificacao.tokenConfirmacao
                );

        try {
            processamentoService.marcarComoProcessando(evento);

            emailService.enviar(evento);

            processamentoService.marcarComoEnviada(evento);

            LOG.infof(
                    "Retry concluído com sucesso para o evento %s",
                    notificacao.eventoId
            );

        } catch (Exception exception) {

            processamentoService.marcarComoFalhaTemporaria(
                    evento,
                    exception
            );

            LOG.errorf(
                    exception,
                    "Falha no retry do evento %s",
                    notificacao.eventoId
            );
        }
    }
}
