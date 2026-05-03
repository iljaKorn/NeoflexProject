package com.neoproject.dossier.service;

import com.neoproject.dossier.model.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String emailAddressFrom;

    private final JavaMailSender mailSender;

    public void finishRegistration(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Завершение оформления");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
    }

    public void createDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Создание документов");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
    }
}
