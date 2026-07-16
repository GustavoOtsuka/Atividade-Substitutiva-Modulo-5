package com.gustavo.application.service;

import com.gustavo.domain.model.EncomendaRecebidaEvento;
import com.gustavo.infrastructure.email.NotificacaoEmailService;
import com.gustavo.application.consumer.ProcessamentoNotificacaoService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificacaoService {

    private final ProcessamentoNotificacaoService processamentoService;
    private final NotificacaoEmailService emailService;

    public NotificacaoService(
            ProcessamentoNotificacaoService processamentoService,
            NotificacaoEmailService emailService
    ) {
        this.processamentoService = processamentoService;
        this.emailService = emailService;
    }

    public void processar(EncomendaRecebidaEvento evento) {

        boolean novo =
                processamentoService.registrarSeNovo(evento);

        if (!novo) {
            return;
        }

        processamentoService.marcarComoProcessando(evento);

        emailService.enviar(evento);

        processamentoService.marcarComoEnviada(evento);
    }

}
