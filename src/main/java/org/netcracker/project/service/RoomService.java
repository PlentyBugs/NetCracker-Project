package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.messaging.Room;
import org.netcracker.project.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExists) {
        return roomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Room::getChatId)
                .or(create(senderId, recipientId, createIfNotExists));
    }

    public String create(String senderId, String recipientId) {
        Optional<Room> room = roomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        if (room.isEmpty()) {
            String chatId = UUID.randomUUID().toString();
            String senderName = userService.findFullNameAndUsernameById(Long.parseLong(senderId));
            String recipientName = userService.findFullNameAndUsernameById(Long.parseLong(recipientId));

            Room senderRecipient = Room
                    .builder()
                    .chatId(chatId)
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .senderName(senderName)
                    .recipientName(recipientName)
                    .build();

            roomRepository.save(senderRecipient);

            if (!senderId.equals(recipientId)) {
                Room recipientSender = Room
                        .builder()
                        .chatId(chatId)
                        .senderId(recipientId)
                        .recipientId(senderId)
                        .senderName(recipientName)
                        .recipientName(senderName)
                        .build();
                roomRepository.save(recipientSender);
            }
            return chatId;
        }
        return room.get().getChatId();
    }

    public Supplier<Optional<String>> create(String senderId, String recipientId, boolean createIfNotExists) {
        return () -> {
            if (!createIfNotExists) return Optional.empty();

            String chatId = create(senderId, recipientId);

            return Optional.of(chatId);
        };
    }

    public List<Room> findAllByUser(User user) {
        return roomRepository.findAllBySenderId(String.valueOf(user.getId()));
    }

    public Map<String, String> findAllUsernamesMapByUser(User user) {
        Map<String, String> map = new HashMap<>();
        findAllByUser(user).stream().map(Room::getRecipientId).forEach(e -> map.put(e, userService.findUsernameById(Long.parseLong(e))));
        return map;
    }

    public Room findBySenderAndRecipientId(String senderId, String recipientId) {
        return roomRepository.findBySenderIdAndRecipientId(senderId, recipientId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
