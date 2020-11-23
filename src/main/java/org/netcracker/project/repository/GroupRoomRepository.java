package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.GroupRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRoomRepository extends MongoRepository<GroupRoom, String> {

    List<GroupRoom> findAllByParticipantIdsContains(String userId);

    GroupRoom findByChatId(String chatId);
}
