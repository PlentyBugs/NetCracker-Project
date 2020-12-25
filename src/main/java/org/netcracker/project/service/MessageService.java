package org.netcracker.project.service;

import org.netcracker.project.model.messaging.Message;

import java.util.List;

public interface MessageService {

    /**
     * Метод, который сохраняет сообщение
     * @param message Сохраняемое сообщение
     * @return Сообщение, которое мы сохранили
     */
    Message save(Message message);

    /**
     * Метод, который возвращает все сообщение в указанном чате
     * @param chatId Id чата
     * @return Список сообщений данного чата
     */
    List<Message> findByChatId(String chatId);
}
