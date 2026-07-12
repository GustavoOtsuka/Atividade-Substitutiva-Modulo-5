package com.gustavo.sistemaencomendas.infrastructure.messaging;

import com.gustavo.sistemaencomendas.domain.model.Encomenda;
import com.gustavo.sistemaencomendas.domain.model.OutboxEvento;
import com.gustavo.sistemaencomendas.domain.model.StatusNotificacao;
import com.gustavo.sistemaencomendas.domain.model.StatusOutbox;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.EncomendaRepository;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.OutboxEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublicadorKafka {

    private static final int MAXIMO_TENTATIVAS = 10;

    private final OutboxEventoRepository outboxEventoRepository;
    private final EncomendaRepository encomendaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topico-encomendas}")
    private String topicoEncomendas;

    @Scheduled(fixedDelay = 5000)
    public void publicarEventosPendentes() {

        List<OutboxEvento> eventos =
                outboxEventoRepository.findTop20ByStatusOrderByCriadoEmAsc(
                        StatusOutbox.PENDENTE
                );

        for (OutboxEvento evento : eventos) {
            publicar(evento);
        }
    }

    private void publicar(OutboxEvento evento) {

        try {
            kafkaTemplate.send(
                    topicoEncomendas,
                    evento.getAgregadoId(),
                    evento.getPayload()
            ).get();

            marcarComoPublicado(evento);

        } catch (Exception exception) {
            registrarFalha(evento, exception);
        }
    }

    @Transactional
    protected void marcarComoPublicado(OutboxEvento evento) {

        evento.setStatus(StatusOutbox.PUBLICADO);
        evento.setPublicadoEm(LocalDateTime.now());
        evento.setUltimoErro(null);

        outboxEventoRepository.save(evento);

        Long encomendaId = Long.valueOf(evento.getAgregadoId());

        Encomenda encomenda = encomendaRepository.findById(encomendaId)
                .orElseThrow(() ->
                        new IllegalStateException("Encomenda do evento não encontrada.")
                );

        encomenda.setStatusNotificacao(
                StatusNotificacao.PUBLICADA_NO_KAFKA
        );

        encomendaRepository.save(encomenda);
    }

    @Transactional
    protected void registrarFalha(
            OutboxEvento evento,
            Exception exception
    ) {

        int tentativas = evento.getTentativas() + 1;

        evento.setTentativas(tentativas);
        evento.setUltimoErro(resumirErro(exception));

        if (tentativas >= MAXIMO_TENTATIVAS) {
            evento.setStatus(StatusOutbox.ERRO);
        }

        outboxEventoRepository.save(evento);
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