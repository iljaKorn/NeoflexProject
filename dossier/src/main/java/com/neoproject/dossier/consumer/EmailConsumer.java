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
}
