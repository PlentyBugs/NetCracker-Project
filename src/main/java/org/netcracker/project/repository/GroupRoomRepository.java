package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.GroupRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRoomRepository extends MongoRepository<GroupRoom, String> {

    /**
     * Метод использующийся для получения всех групповых чатов, в которых состоит пользователь
     * @param userId Id пользователя, групповые чаты которого запрашиваются
     * @return Список групповых чатов, которые содержат пользователя с данным Id
     */
    List<GroupRoom> findAllByParticipantIdsContains(String userId);

    /**
     * Метод использующийся для получения группового чата по его Id
     * @param chatId Id группового чата
     * @return Групповой чат с указанным Id
     */
    GroupRoom findByChatId(String chatId);
}
