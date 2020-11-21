package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<Room> findAllBySenderIdOrRecipientId(String senderId, String recipientId);

    Room findByChatId(String chatId);
}
