package org.netcracker.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.messaging.Message;
import org.netcracker.project.repository.MessageRepository;
import org.netcracker.project.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;

    /**
     * Метод, который сохраняет сообщение
     * @param message Сохраняемое сообщение
     * @return Сообщение, которое мы сохранили
     */
    public Message save(Message message) {
        return repository.save(message);
    }

    /**
     * Метод, который возвращает все сообщение в указанном чате
     * @param chatId Id чата
     * @return Список сообщений данного чата
     */
    public List<Message> findByChatId(String chatId) {
        return repository.findByChatId(chatId);
    }
}
