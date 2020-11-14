package org.netcracker.project.repository;

import org.netcracker.project.model.enums.MessageStatus;
import org.netcracker.project.model.messaging.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    Long countBySenderIdAndRecipientIdAndStatus(String senderId, String recipientId, MessageStatus status);

    List<Message> findByChatId(String chatId);
}
