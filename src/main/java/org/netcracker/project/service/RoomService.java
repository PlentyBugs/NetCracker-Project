package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.ChatStatus;
import org.netcracker.project.model.messaging.ChatNotification;
import org.netcracker.project.model.messaging.GroupRoom;
import org.netcracker.project.model.messaging.Room;
import org.netcracker.project.repository.GroupRoomRepository;
import org.netcracker.project.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroupRoomRepository groupRoomRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;

    /**
     * Метод, возвращающий Optional со строкой, содержащей Id чата
     * @param senderId Id пользователя - отправителя
     * @param recipientId Id пользователя - получателя
     * @param createIfNotExists Булево значение. Если true - создать новый чат для указанных пользователей, false - ничего не делать
     * @return Optional, содержащий строку с Id чата, либо пустой, если чат не был найден и createIfNotExists = false
     */
    public Optional<String> getRoomId(String senderId, String recipientId, boolean createIfNotExists) {
        return roomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Room::getChatId)
                .or(createRoom(senderId, recipientId, createIfNotExists));
    }

    /**
     * Метод, создающий приватный чат
     * @param senderId Id одного пользователя
     * @param recipientId Id - второго пользователя
     * @return Id чата
     */
    public String createRoom(String senderId, String recipientId) {
        Optional<Room> room = roomRepository.findBySenderIdAndRecipientId(senderId, recipientId).or(() -> roomRepository.findBySenderIdAndRecipientId(recipientId, senderId));
        if (room.isEmpty()) {
            String chatId = UUID.randomUUID().toString();
            String senderName = userService.findFullNameAndUsernameById(Long.parseLong(senderId));
            String recipientName = userService.findFullNameAndUsernameById(Long.parseLong(recipientId));
            String chatName = senderId.equals(recipientId) ? "You": senderName + " and " + recipientName;

            Room senderRecipient = Room
                    .builder()
                    .chatId(chatId)
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .chatName(chatName)
                    .build();

            roomRepository.save(senderRecipient);

            sendPersonalNotification(senderRecipient, senderId, userService.findUsernameById(Long.parseLong(recipientId)), ChatStatus.ADD);

            if (!senderId.equals(recipientId)) {
                sendPersonalNotification(senderRecipient, recipientId, userService.findUsernameById(Long.parseLong(senderId)), ChatStatus.ADD);
            }

            return chatId;
        }
        return room.get().getChatId();
    }

    /**
     * Метод, который создает комнату, и возвращает Supplier
     * @param senderId Id одного пользователя
     * @param recipientId Id второго пользователя
     * @param createIfNotExists Булево значение. Если true - создать новый чат для указанных пользователей, false - ничего не делать
     * @return Supplier который возвращает Optional, содержащий строку с Id чата, либо пустой, если чат не был найден и createIfNotExists = false
     */
    public Supplier<Optional<String>> createRoom(String senderId, String recipientId, boolean createIfNotExists) {
        return () -> {
            if (!createIfNotExists) return Optional.empty();

            String chatId = createRoom(senderId, recipientId);

            return Optional.of(chatId);
        };
    }

    /**
     * Метод, который создает Групповой Чат
     * @param adminId Id пользователя, который будет назначен админом этого чата
     * @param participantIds Множество строк с Id участников группового чата
     * @param chatName Название группового чата
     */
    public void createGroupRoom(String adminId, Set<String> participantIds, String chatName) {
        String chatId = UUID.randomUUID().toString();
        createGroupRoomWithGivenChatId(adminId, participantIds, chatName, chatId);
    }

    /**
     * Метод, который создает групповой чат с указанным Id
     * @param adminId Id пользователя, который будет назначен админом этого чата
     * @param participantIds Множество строк с Id участников группового чата
     * @param chatName Название группового чата
     * @param chatId Id группового чата
     */
    public void createGroupRoomWithGivenChatId(String adminId, Set<String> participantIds, String chatName, String chatId) {
        GroupRoom groupRoom = GroupRoom
                .builder()
                .chatId(chatId)
                .chatName(chatName)
                .adminId(adminId)
                .participantIds(participantIds)
                .build();

        groupRoomRepository.save(groupRoom);

        participantIds.forEach(id -> sendGroupNotification(groupRoom, id, ChatStatus.ADD));
    }

    /**
     * Метод, который возвращает все групповые чаты по пользователю
     * @param user Пользователь, по которому будет производиться поиск групповых чатов
     * @return Список найденных групповых чатов
     */
    public List<GroupRoom> findAllGroupRoomsByUser(User user) {
        return groupRoomRepository.findAllByParticipantIdsContains(user.getId().toString());
    }

    /**
     * Метод, который возвращает все приватные чаты по пользователю
     * @param user Пользователь, по которому будет производиться поиск чатов
     * @return Список найденных чатов
     */
    public List<Room> findAllRoomsByUser(User user) {
        String id = user.getId().toString();
        return roomRepository.findAllBySenderIdOrRecipientId(id, id);
    }

    /**
     * Метод который возвращает Map, где ключ - Id получателя, а значение - ник получателя
     * Поиск ведется по приватным чатам пользователя
     * @param user Пользователь, по списку приватных чатов которого производится поиск
     * @return Map, где ключ - Id получателя, а значение - ник получателя
     */
    public Map<String, String> findAllUsernamesMapBySender(User user) {
        String id = user.getId().toString();
        Map<String, String> map = new HashMap<>();
        roomRepository.findAllBySenderId(id).stream().map(Room::getRecipientId).forEach(e -> map.put(e, userService.findUsernameById(Long.parseLong(e))));
        return map;
    }

    /**
     * Метод который возвращает Map, где ключ - Id отправителя, а значение - ник отправителя
     * Поиск ведется по приватным чатам пользователя
     * @param user Пользователь, по списку приватных чатов которого производится поиск
     * @return Map, где ключ - Id отправителя, а значение - ник отправителя
     */
    public Map<String, String> findAllUsernamesMapByRecipient(User user) {
        String id = user.getId().toString();
        Map<String, String> map = new HashMap<>();
        roomRepository.findAllByRecipientId(id).stream().map(Room::getSenderId).forEach(e -> map.put(e, userService.findUsernameById(Long.parseLong(e))));
        return map;
    }

    /**
     * Метод, который заполняет Model для страницы мессенджера
     * @param model Model полученная из контроллера, в которую передаются объекты
     * @param user Авторизованный пользователь, чьи чаты будут показаны
     */
    public void getPage(Model model, User user) {
        model.addAttribute("chats", findAllRoomsByUser(user));
        model.addAttribute("groupChats", findAllGroupRoomsByUser(user));
        model.addAttribute("recipientUsernames", findAllUsernamesMapBySender(user));
        model.addAttribute("senderUsernames", findAllUsernamesMapByRecipient(user));
    }

    /**
     * Метод, который возвращает групповой чат по его Id
     * @param chatId Id группового чата
     * @return Групповой чат с данным Id
     */
    public GroupRoom findGroupRoomByChatId(String chatId) {
        return groupRoomRepository.findByChatId(chatId);
    }

    /**
     * Метод, который возвращает приватный чат по его Id
     * @param chatId Id приватного чата
     * @return Приватный чат с данным Id
     */
    public Room findRoomByChatId(String chatId) {
        return roomRepository.findByChatId(chatId);
    }

    /**
     * Метод, который добавляет пользователя в групповой чат
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     */
    public void addGroupMember(String groupChatId, String userId) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().add(userId);
        groupRoomRepository.save(groupRoom);
        sendGroupNotification(groupRoom, userId, ChatStatus.ADD);
    }

    /**
     * Метод, который удаляет пользователя из группового чата
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     */
    public void removeGroupMember(String groupChatId, String userId) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().remove(userId);
        groupRoomRepository.save(groupRoom);
        sendGroupNotification(groupRoom, userId, ChatStatus.REMOVE);
    }

    /**
     *
     * Метод, который удаляет пользователя из группового чата
     * Используется для того, чтобы исключать людей из группового чата
     * Используется администратором чата
     * @param groupChatId Id группового чата
     * @param userId Id добавляемого пользователя
     * @param adminId Id администратора чата
     */
    public void removeGroupMemberWithAuthCheck(String groupChatId, String userId, String adminId) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        if (!groupRoom.getAdminId().equals(adminId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        groupRoom.getParticipantIds().remove(userId);
        groupRoomRepository.save(groupRoom);
        sendGroupNotification(groupRoom, userId, ChatStatus.REMOVE);
    }

    /**
     * Метод, который добавляет в групповой чат множество пользователей
     * @param groupChatId Id группового чата
     * @param userIds Множество строк содержащих Id добавляемых пользователей
     */
    public void addGroupMembers(String groupChatId, Set<String> userIds) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().addAll(userIds);
        groupRoomRepository.save(groupRoom);
        userIds.forEach(id -> sendGroupNotification(groupRoom, id, ChatStatus.ADD));
    }

    /**
     * Метод, который удаляет из группового чата множество пользователей
     * @param groupChatId Id группового чата
     * @param userIds Множество строк содержащих Id удаляемых пользователей
     */
    public void removeGroupMembers(String groupChatId, Set<String> userIds) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().removeAll(userIds);
        groupRoomRepository.save(groupRoom);
        userIds.forEach(id -> sendGroupNotification(groupRoom, id, ChatStatus.REMOVE));
    }

    /**
     * Метод, который возвращает участников группового чата
     * @param chatId Id группового чата
     * @param user Пользователь, который делает запрос
     * @return Множество DTO SimpleUser, состоящее из участников группового чата
     */
    public Set<SimpleUser> getSimpleParticipantsGroup(String chatId, User user) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(chatId);
        if (!groupRoom.getParticipantIds().contains(user.getId().toString())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return groupRoom.getParticipantIds().stream().map(Long::parseLong).map(userService::findSimpleUserById).collect(Collectors.toSet());
    }

    /**
     * Метод, который возвращает участников приватного чата
     * @param chatId Id приватного чата
     * @param user Пользователь, который делает запрос
     * @return Множество DTO SimpleUser, состоящее из участников приватного чата
     */
    public Set<SimpleUser> getSimpleParticipants(String chatId, User user) {
        Room room = roomRepository.findByChatId(chatId);
        Set<String> participants = room.getRecipientId().equals(room.getSenderId()) ? Set.of(room.getSenderId()): Set.of(room.getSenderId(), room.getRecipientId());
        if (!participants.contains(user.getId().toString())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return participants.stream().map(Long::parseLong).map(userService::findSimpleUserById).collect(Collectors.toSet());
    }

    /**
     * Метод, который отправляет уведомление пользователю из группового чата
     * @param room Групповой чат
     * @param userId Id пользователя, которому отправляется уведомление
     * @param status Статус сообщения: Add, Remove
     */
    public void sendGroupNotification(GroupRoom room, String userId, ChatStatus status) {
        messagingTemplate.convertAndSendToUser(
                userId, "/queue/chats",
                new ChatNotification(
                        room.getChatId(),
                        room.getChatName(),
                        room.getChatId(),
                        status,
                        true
                )
        );
    }

    /**
     * Метод, который отправляет уведомление пользователю приватного чата
     * @param room Приватный чат
     * @param recipientId Id получателя
     * @param recipientName Имя получателя
     * @param status Статус сообщения: Add, Remove
     */
    public void sendPersonalNotification(Room room, String recipientId, String recipientName, ChatStatus status) {
        messagingTemplate.convertAndSendToUser(
                recipientId, "/queue/chats",
                new ChatNotification(
                        room.getChatId(),
                        recipientName,
                        room.getRecipientId(),
                        status,
                        false
                )
        );
    }
}
