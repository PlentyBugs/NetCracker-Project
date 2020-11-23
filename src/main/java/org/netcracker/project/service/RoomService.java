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

    public Optional<String> getRoomId(String senderId, String recipientId, boolean createIfNotExists) {
        return roomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Room::getChatId)
                .or(createRoom(senderId, recipientId, createIfNotExists));
    }

    public String createRoom(String senderId, String recipientId) {
        Optional<Room> room = roomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
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

    public Supplier<Optional<String>> createRoom(String senderId, String recipientId, boolean createIfNotExists) {
        return () -> {
            if (!createIfNotExists) return Optional.empty();

            String chatId = createRoom(senderId, recipientId);

            return Optional.of(chatId);
        };
    }

    public String createGroupRoom(String adminId, Set<String> participantIds, String chatName) {
        String chatId = UUID.randomUUID().toString();

        createGroupRoomWithGivenChatId(adminId, participantIds, chatName, chatId);

        return chatId;
    }

    public void createGroupRoomWithGivenChatId(String adminId, Set<String> participantIds, String chatName, String chatId) {
        GroupRoom groupRoom = GroupRoom
                .builder()
                .chatId(chatId)
                .chatName(chatName)
                .adminId(adminId)
                .participantIds(participantIds)
                .build();

        groupRoomRepository.save(groupRoom);
    }

    public List<GroupRoom> findAllGroupRoomsByUser(User user) {
        return groupRoomRepository.findAllByParticipantIdsContains(user.getId().toString());
    }

    public List<Room> findAllRoomsByUser(User user) {
        String id = user.getId().toString();
        return roomRepository.findAllBySenderIdOrRecipientId(id, id);
    }

    public Map<String, String> findAllUsernamesMapByUser(User user) {
        Map<String, String> map = new HashMap<>();
        findAllRoomsByUser(user).stream().map(Room::getRecipientId).forEach(e -> map.put(e, userService.findUsernameById(Long.parseLong(e))));
        return map;
    }

    public void getPage(Model model, User user) {
        model.addAttribute("chats", findAllRoomsByUser(user));
        model.addAttribute("groupChats", findAllGroupRoomsByUser(user));
        model.addAttribute("usernames", findAllUsernamesMapByUser(user));
    }

    public GroupRoom findGroupRoomByChatId(String chatId) {
        return groupRoomRepository.findByChatId(chatId);
    }

    public Room findRoomByChatId(String chatId) {
        return roomRepository.findByChatId(chatId);
    }

    public void addGroupMember(String groupChatId, String userId) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().add(userId);
        groupRoomRepository.save(groupRoom);
        sendGroupNotification(groupRoom, userId, ChatStatus.ADD);
    }

    public void removeGroupMember(String groupChatId, String userId) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().remove(userId);
        groupRoomRepository.save(groupRoom);
        sendGroupNotification(groupRoom, userId, ChatStatus.REMOVE);
    }

    public void addGroupMembers(String groupChatId, Set<String> userIds) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().addAll(userIds);
        groupRoomRepository.save(groupRoom);
        userIds.forEach(id -> sendGroupNotification(groupRoom, id, ChatStatus.ADD));
    }

    public void removeGroupMembers(String groupChatId, Set<String> userIds) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(groupChatId);
        groupRoom.getParticipantIds().removeAll(userIds);
        groupRoomRepository.save(groupRoom);
        userIds.forEach(id -> sendGroupNotification(groupRoom, id, ChatStatus.REMOVE));
    }

    public Set<SimpleUser> getSimpleParticipantsGroup(String chatId, User user) {
        GroupRoom groupRoom = groupRoomRepository.findByChatId(chatId);
        if (!groupRoom.getParticipantIds().contains(user.getId().toString())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return groupRoom.getParticipantIds().stream().map(Long::parseLong).map(userService::findSimpleUserById).collect(Collectors.toSet());
    }

    public Set<SimpleUser> getSimpleParticipants(String chatId, User user) {
        Room room = roomRepository.findByChatId(chatId);
        Set<String> participants = Set.of(room.getSenderId(), room.getRecipientId());
        if (!participants.contains(user.getId().toString())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return participants.stream().map(Long::parseLong).map(userService::findSimpleUserById).collect(Collectors.toSet());
    }

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
