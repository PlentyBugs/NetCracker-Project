package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
