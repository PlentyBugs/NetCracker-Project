package org.netcracker.project.service;

public interface MailService {

    /**
     * Метод, который асинхронно посылает письмо с сообщением на указанный email
     * @param emailTo Email-получатель
     * @param subject Тема сообщения
     * @param message Контент сообщения
     */
    void send(String emailTo, String subject, String message);
}
