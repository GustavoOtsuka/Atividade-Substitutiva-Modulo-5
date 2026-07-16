package com.gustavo.infrastructure.email;

import com.gustavo.domain.model.EncomendaRecebidaEvento;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificacaoEmailService {

    private final Mailer mailer;

    public NotificacaoEmailService(Mailer mailer) {
        this.mailer = mailer;
    }

    public void enviar(EncomendaRecebidaEvento evento) {




        String assunto = "Nova encomenda recebida";

        String mensagem = """
                Olá, %s.

                A portaria recebeu uma nova encomenda para você.

                Unidade: %s
                Descrição: %s
                Data do recebimento: %s

                Esta é uma mensagem automática do Sistema de Encomendas.
                """.formatted(
                evento.nomeMorador(),
                evento.apartamento(),
                evento.descricao(),
                evento.dataRecebimento()
        );

        mailer.send(
                Mail.withText(
                        evento.emailMorador(),
                        assunto,
                        mensagem
                )
        );
    }
}
