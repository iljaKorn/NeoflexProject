package com.neoproject.deal.producer;

import com.neoproject.deal.model.dto.EmailMessage;
import com.neoproject.deal.model.enums.Theme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {

    @Value("${spring.kafka.topics.finish-registration}")
    private String finishRegistrationTopic;

    @Value("${spring.kafka.topics.create-documents}")
    private String createDocumentTopic;

    private final KafkaTemplate<String , EmailMessage> kafkaTemplate;

    public void produceMessageForFinishRegistration(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.FINISH_REGISTRATION);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Ваша заявка предварительно одобрена, завершите оформление.");

        kafkaTemplate.send(finishRegistrationTopic, emailMessage);
    }

    public void produceMessageForCreateDocument(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.CREATE_DOCUMENTS);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Перейти к оформлению документов.");

        kafkaTemplate.send(createDocumentTopic, emailMessage);
    }
}
