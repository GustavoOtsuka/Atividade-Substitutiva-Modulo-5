package com.gustavo.application.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ProcessamentoNotificacaoService processamentoService;

    public EncomendaRecebidaConsumer(
            ObjectMapper objectMapper,
            ProcessamentoNotificacaoService processamentoService
    ) {
        this.objectMapper = objectMapper;
        this.processamentoService = processamentoService;
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

            boolean eventoNovo =
                    processamentoService.registrarSeNovo(evento);

            if (!eventoNovo) {
                LOG.warnf(
                        "Evento duplicado ignorado: %s",
                        evento.eventoId()
                );
                return;
            }

            LOG.info("=========================================");
            LOG.info("Evento registrado para processamento");
            LOG.infof("Evento: %s", evento.eventoId());
            LOG.infof("Morador: %s", evento.nomeMorador());
            LOG.infof("E-mail: %s", evento.emailMorador());
            LOG.infof("Descrição: %s", evento.descricao());
            LOG.info("=========================================");

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
