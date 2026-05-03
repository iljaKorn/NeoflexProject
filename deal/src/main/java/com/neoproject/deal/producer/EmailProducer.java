package com.neoproject.deal.producer;

import com.neoproject.deal.model.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {
    private final KafkaTemplate<String , EmailMessage> kafkaTemplate;

    public void produceMessage(String topic, EmailMessage message) {
        kafkaTemplate.send(topic, message);
    }
}
