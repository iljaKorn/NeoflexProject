package com.neoproject.dossier.consumer;

import com.neoproject.dossier.model.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    @KafkaListener(topics = "${spring.kafka.topics.finish-registration}")
    public void consumeOrder(EmailMessage message) {
        System.out.println(message.getText());
    }
}
