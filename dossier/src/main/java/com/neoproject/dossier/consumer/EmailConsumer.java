package com.neoproject.dossier.consumer;

import com.neoproject.dossier.service.MailService;
import com.neoproject.dossier.model.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final MailService mailService;

    @KafkaListener(topics = "${spring.kafka.topics.finish-registration}")
    public void consumeMessageForFinishRegistration(EmailMessage message) {
        log.info("Пришло сообщение с данными для завершения регистрации: {}", message);
        mailService.finishRegistration(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.create-documents}")
    public void consumeMessageForCreateDocuments(EmailMessage message) {
        log.info("Пришло сообщение с данными для создания документов: {}", message);
        mailService.createDocuments(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.send-documents}")
    public void consumeMessageForSendDocuments(EmailMessage message) {
        log.info("Пришло сообщение с данными для отправки документов: {}", message);
        mailService.sendDocuments(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.send-ses}")
    public void consumeMessageForRequestToSignDocuments(EmailMessage message) {
        log.info("Пришло сообщение с данными для отправки кода: {}", message);
        mailService.requestToSignDocuments(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.credit-issued}")
    public void consumeMessageForSignDocuments(EmailMessage message) {
        log.info("Пришло сообщение с данными для одобрения кредита: {}", message);
        mailService.signDocuments(message);
    }

    @KafkaListener(topics = "${spring.kafka.topics.statement-denied}")
    public void consumeMessageForStatementDenied(EmailMessage message) {
        log.info("Пришло сообщение с данными для отмены заявки: {}", message);
        mailService.deniedStatement(message);
    }
}
