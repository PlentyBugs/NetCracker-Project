package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.messaging.Message;
import org.netcracker.project.model.messaging.MessageNotification;
import org.netcracker.project.service.MessageService;
import org.netcracker.project.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private SimpMessagingTemplate messagingTemplate;
    private MessageService messageService;
    private RoomService roomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        Optional<String> chatId = roomService.getChatId(message.getSenderId(), message.getRecipientId(), true);
        message.setChatId(chatId.get());

        Message savedMessage = messageService.save(message);

        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages",
                new MessageNotification(
                        savedMessage.getId(),
                        savedMessage.getSenderId(),
                        savedMessage.getRecipientId()
                )
        );
    }
}
