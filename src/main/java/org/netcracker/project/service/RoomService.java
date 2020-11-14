package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.messaging.Room;
import org.netcracker.project.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RoomService {

    private RoomRepository roomRepository;

    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExists) {
        return roomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Room::getChatId)
                .or(create(senderId, recipientId, createIfNotExists));
    }

    private Supplier<Optional<String>> create(String senderId, String recipientId, boolean createIfNotExists) {
        return () -> {
            if (!createIfNotExists) return Optional.empty();

            String chatId = UUID.randomUUID().toString();

            Room senderRecipient = Room
                    .builder()
                    .chatId(chatId)
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .build();

            Room recipientSender = Room
                    .builder()
                    .chatId(chatId)
                    .senderId(recipientId)
                    .recipientId(senderId)
                    .build();

            roomRepository.save(senderRecipient);
            roomRepository.save(recipientSender);

            return Optional.of(chatId);
        };
    }
}
