package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.messaging.Message;
import org.netcracker.project.repository.MessageRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository repository;
    private final RoomService chatRoomService;
    private final MongoOperations mongoOperations;

    /**
     * Метод, который сохраняет сообщение
     * @param message - Сохраняемое сообщение
     * @return - Сообщение, которое мы сохранили
     */
    public Message save(Message message) {
        return repository.save(message);
    }

    /**
     * Метод, который возвращает все сообщение в указанном чате
     * @param chatId - Id чата
     * @return - Список сообщений данного чата
     */
    public List<Message> findByChatId(String chatId) {
        return repository.findByChatId(chatId);
    }
}
