package com.gustavo.application.consumer;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EncomendaRecebidaConsumer {

    private static final Logger LOG =
            Logger.getLogger(EncomendaRecebidaConsumer.class);

    @Incoming("encomendas")
    @Blocking
    public void consumir(String mensagem) {

        LOG.info("=========================================");
        LOG.info("Nova encomenda recebida pelo Quarkus");
        LOG.info(mensagem);
        LOG.info("=========================================");
    }
}
