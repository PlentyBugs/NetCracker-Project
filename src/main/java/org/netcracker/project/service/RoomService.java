package org.netcracker.project.service;

import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.ChatStatus;
import org.netcracker.project.model.messaging.GroupRoom;
import org.netcracker.project.model.messaging.Room;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface RoomService {

    /**
     * Метод, возвращающий Optional со строкой, содержащей Id чата
     * @param senderId Id пользователя - отправителя
     * @param recipientId Id пользователя - получателя
     * @param createIfNotExists Булево значение. Если true - создать новый чат для указанных пользователей, false - ничего не делать
     * @return Optional, содержащий строку с Id чата, либо пустой, если чат не был найден и createIfNotExists = false
     */
    Optional<String> getRoomId(String senderId, String recipientId, boolean createIfNotExists);

    /**
     * Метод, создающий приватный чат
     * @param senderId Id одного пользователя
     * @param recipientId Id - второго пользователя
     * @return Id чата
     */
    String createRoom(String senderId, String recipientId);

    /**
     * Метод, который создает комнату, и возвращает Supplier
     * @param senderId Id одного пользователя
     * @param recipientId Id второго пользователя
     * @param createIfNotExists Булево значение. Если true - создать новый чат для указанных пользователей, false - ничего не делать
     * @return Supplier который возвращает Optional, содержащий строку с Id чата, либо пустой, если чат не был найден и createIfNotExists = false
     */
    Supplier<Optional<String>> createRoom(String senderId, String recipientId, boolean createIfNotExists);

    /**
     * Метод, который создает Групповой Чат
     * @param adminId Id пользователя, который будет назначен админом этого чата
     * @param participantIds Множество строк с Id участников группового чата
     * @param chatName Название группового чата
     */
    void createGroupRoom(String adminId, Set<String> participantIds, String chatName);

    /**
     * Метод, который создает групповой чат с указанным Id
     * @param adminId Id пользователя, который будет назначен админом этого чата
     * @param participantIds Множество строк с Id участников группового чата
     * @param chatName Название группового чата
     * @param chatId Id группового чата
     */
    void createGroupRoomWithGivenChatId(String adminId, Set<String> participantIds, String chatName, String chatId);

    /**
     * Метод, который возвращает все групповые чаты по пользователю
     * @param user Пользователь, по которому будет производиться поиск групповых чатов
     * @return Список найденных групповых чатов
     */
    List<GroupRoom> findAllGroupRoomsByUser(User user);

    /**
     * Метод, который возвращает все приватные чаты по пользователю
     * @param user Пользователь, по которому будет производиться поиск чатов
     * @return Список найденных чатов
     */
    List<Room> findAllRoomsByUser(User user);

    /**
     * Метод который возвращает Map, где ключ - Id получателя, а значение - ник получателя
     * Поиск ведется по приватным чатам пользователя
     * @param user Пользователь, по списку приватных чатов которого производится поиск
     * @return Map, где ключ - Id получателя, а значение - ник получателя
     */
    Map<String, String> findAllUsernamesMapBySender(User user);

    /**
     * Метод который возвращает Map, где ключ - Id отправителя, а значение - ник отправителя
     * Поиск ведется по приватным чатам пользователя
     * @param user Пользователь, по списку приватных чатов которого производится поиск
     * @return Map, где ключ - Id отправителя, а значение - ник отправителя
     */
    Map<String, String> findAllUsernamesMapByRecipient(User user);

    /**
     * Метод, который заполняет Model для страницы мессенджера
     * @param model Model полученная из контроллера, в которую передаются объекты
     * @param user Авторизованный пользователь, чьи чаты будут показаны
     */
    void getPage(Model model, User user);

    /**
     * Метод, который возвращает групповой чат по его Id
     * @param chatId Id группового чата
     * @return Групповой чат с данным Id
     */
    GroupRoom findGroupRoomByChatId(String chatId);

    /**
     * Метод, который возвращает приватный чат по его Id
     * @param chatId Id приватного чата
     * @return Приватный чат с данным Id
     */
    Room findRoomByChatId(String chatId);

    /**
     * Метод, который добавляет пользователя в групповой чат
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     */
    void addGroupMember(String groupChatId, String userId);

    /**
     * Метод, который удаляет пользователя из группового чата
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     */
    void removeGroupMember(String groupChatId, String userId);

    /**
     *
     * Метод, который удаляет пользователя из группового чата
     * Используется для того, чтобы исключать людей из группового чата
     * Используется администратором чата
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     * @param adminId Id администратора чата
     */
    void removeGroupMemberWithAuthCheck(String groupChatId, String userId, String adminId);

    /**
     * Метод, который добавляет в групповой чат множество пользователей
     * @param groupChatId Id группового чата
     * @param userIds Множество строк содержащих Id добавляемых пользователей
     */
    void addGroupMembers(String groupChatId, Set<String> userIds);

    /**
     * Метод, который удаляет из группового чата множество пользователей
     * @param groupChatId Id группового чата
     * @param userIds Множество строк содержащих Id удаляемых пользователей
     */
    void removeGroupMembers(String groupChatId, Set<String> userIds);

    /**
     * Метод, который возвращает участников группового чата
     * @param chatId Id группового чата
     * @param user Пользователь, который делает запрос
     * @return Множество DTO SimpleUser, состоящее из участников группового чата
     */
    Set<SimpleUser> getSimpleParticipantsGroup(String chatId, User user);

    /**
     * Метод, который возвращает участников приватного чата
     * @param chatId Id приватного чата
     * @param user Пользователь, который делает запрос
     * @return Множество DTO SimpleUser, состоящее из участников приватного чата
     */
    Set<SimpleUser> getSimpleParticipants(String chatId, User user);

    /**
     * Метод, который отправляет уведомление пользователю из группового чата
     * @param room Групповой чат
     * @param userId Id пользователя, которому отправляется уведомление
     * @param status Статус сообщения: Add, Remove
     */
    void sendGroupNotification(GroupRoom room, String userId, ChatStatus status);

    /**
     * Метод, который отправляет уведомление пользователю приватного чата
     * @param room Приватный чат
     * @param recipientId Id получателя
     * @param recipientName Имя получателя
     * @param status Статус сообщения: Add, Remove
     */
    void sendPersonalNotification(Room room, String recipientId, String recipientName, ChatStatus status);
}
