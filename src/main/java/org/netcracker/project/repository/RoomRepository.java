package org.netcracker.project.repository;

import org.netcracker.project.model.messaging.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    /**
     * Метод используется для получения Optional с приватным чатом по двум его участникам
     * Существует только один чат с таким набором id, т.е. пользователи с id 2 и 3
     * могут иметь только один приватный чат между собой, и зависеть его конфигурация будет от того, кто первый создал.
     * Т.е. будет существовать (2, 3), а (3, 2) уже не может быть создано, поэтому, если не будет найден результат при
     * вызове findBySenderIdAndRecipientId(3, 2), следует вызвать findBySenderIdAndRecipientId(2, 3), чтобы получить
     * желаемый приватный чат.
     * todo: В будущем планируется оптимизировать запрос и делать проверку sId == argsId && rId == argRId || rId == argsId && sId == argRId
     * Хотя, если такое возможно, то стоит пользоваться поиском чатов при помощи Id чата
     * @param senderId - Id одного участника приватного чата, считающегося отправителем в чате
     * @param recipientId - Id второго участника приватного чата, считающегося получателем в чате
     * @return - Optional содержащий приватный чат с указанными участниками или пустой Optional, если чат не был найден
     */
    Optional<Room> findBySenderIdAndRecipientId(String senderId, String recipientId);

    /**
     * Метод используется для получения всех приватных чатов с указанными пользователями в указанной конфигурации
     * Т.е. либо room.senderId == senderId || room.recipientId == recipientId
     * Если вызвать метод с одинаковыми id, то будут найдены все приватные чаты пользователя
     * @param senderId - Id первого участника приватного чата, считающегося отправителем в чате
     * @param recipientId - Id второго участника приватного чата, считающегося получателем в чате
     * @return - Список приватных чатов с указанными пользователями в указанной конфигурации
     */
    List<Room> findAllBySenderIdOrRecipientId(String senderId, String recipientId);

    /**
     * Метод используется для получения приватного чата по его Id
     * @param chatId - Id приватного чата
     * @return - Групповой чат с указанным id, если был найден, и null в ином случае
     */
    Room findByChatId(String chatId);

    /**
     * Метод, который используется для получения списка приватных чатов, в которых пользователь с указанным Id выступает
     * в роли создателя приватного чата.
     * @param id - Id участника приватного чата, считающегося отправителем/создателем приватного чата
     * @return - Список приватных чатов для пользователя с данным id для данной конфигурации
     */
    List<Room> findAllBySenderId(String id);

    /**
     * Метод, который используется для получения списка приватных чатов, в которых пользователь с указанным id выступает
     * в роли второго участника чата, т.е. он не является его создателем
     * @param id - Id второго участника приватного чата, т.е. пользователь не создавал этот приватный чат
     * @return - Список приватных чатов для пользователя с данным id для данной конфигурации
     */
    List<Room> findAllByRecipientId(String id);
}
