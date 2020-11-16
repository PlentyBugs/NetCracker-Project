package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.enums.MessageStatus;
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


    public Message save(Message message) {
        message.setStatus(MessageStatus.RECEIVED);
        return repository.save(message);
    }

    public List<Message> findByChatId(String chatId) {
        return repository.findByChatId(chatId);
    }
}
