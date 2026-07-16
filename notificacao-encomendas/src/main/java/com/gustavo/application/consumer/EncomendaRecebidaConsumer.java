package com.gustavo.application.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.domain.model.EncomendaRecebidaEvento;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EncomendaRecebidaConsumer {

    private static final Logger LOG =
            Logger.getLogger(EncomendaRecebidaConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Incoming("encomendas")
    @Blocking
    public void consumir(String mensagem) {

        try {
            EncomendaRecebidaEvento evento =
                    objectMapper.readValue(
                            mensagem,
                            EncomendaRecebidaEvento.class
                    );

            LOG.info("=========================================");
            LOG.info("Nova encomenda recebida pelo Quarkus");
            LOG.infof("Evento: %s", evento.eventoId());
            LOG.infof("Morador: %s", evento.nomeMorador());
            LOG.infof("E-mail: %s", evento.emailMorador());
            LOG.infof("Apartamento: %s", evento.apartamento());
            LOG.infof("Descrição: %s", evento.descricao());
            LOG.info("=========================================");

        } catch (Exception exception) {
            LOG.error(
                    "Erro ao converter evento de encomenda.",
                    exception
            );

            throw new IllegalStateException(
                    "Não foi possível processar o evento.",
                    exception
            );
        }
    }
}
