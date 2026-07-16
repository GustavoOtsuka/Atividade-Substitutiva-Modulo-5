package com.gustavo.application.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.application.service.NotificacaoService;
import com.gustavo.domain.model.EncomendaRecebidaEvento;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EncomendaRecebidaConsumer {

    private static final Logger LOG =
            Logger.getLogger(EncomendaRecebidaConsumer.class);

    private final ObjectMapper objectMapper;
    private final NotificacaoService notificacaoService;

    public EncomendaRecebidaConsumer(
            ObjectMapper objectMapper,
            NotificacaoService notificacaoService
    ) {
        this.objectMapper = objectMapper;
        this.notificacaoService = notificacaoService;
    }

    @Incoming("encomendas")
    @Blocking
    public void consumir(String mensagem) {

        try {
            EncomendaRecebidaEvento evento =
                    objectMapper.readValue(
                            mensagem,
                            EncomendaRecebidaEvento.class
                    );

            notificacaoService.processar(evento);

            LOG.infof(
                    "Evento processado: %s",
                    evento.eventoId()
            );

        } catch (Exception exception) {
            LOG.error(
                    "Erro ao processar evento de encomenda.",
                    exception
            );

            throw new IllegalStateException(
                    "Não foi possível processar o evento.",
                    exception
            );
        }
    }
}
