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

    public void sendDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Документы на кредит");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
    }

    public void requestToSignDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Подписание документов");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
    }

    public void signDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Выдача кредита");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
    }
}
