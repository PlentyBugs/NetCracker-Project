package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    /**
     * Метод используется для получения списка сообщений для данного чата по его id.
     * Не важно, групповой чат это или приватный.
     * @param chatId Id чата, сообщения которого запрашиваются. Приватный он или групповой - не имеет значения
     * @return Список сообщений чата, если таковой был найден, иначе null
     */
    List<Message> findByChatId(String chatId);
}
