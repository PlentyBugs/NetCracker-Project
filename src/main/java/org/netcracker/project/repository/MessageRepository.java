package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    Long countBySenderIdAndRecipientId(String senderId, String recipientId);

    List<Message> findByChatId(String chatId);
}
