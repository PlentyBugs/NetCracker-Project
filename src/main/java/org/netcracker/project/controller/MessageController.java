package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.messaging.Message;
import org.netcracker.project.model.messaging.MessageNotification;
import org.netcracker.project.model.messaging.Room;
import org.netcracker.project.service.MessageService;
import org.netcracker.project.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final RoomService roomService;

    @GetMapping("/messenger")
    public String getPage(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        model.addAttribute("chats", roomService.findAllByUser(user));
        model.addAttribute("usernames", roomService.findAllUsernamesMapByUser(user));
        return "messenger";
    }

    @GetMapping(value = "/messenger/{id}/chat/{recipientId}", produces = "application/json")
    @ResponseBody
    public Room getChat(
            @AuthenticationPrincipal User user,
            @PathVariable("id") User sender,
            @PathVariable() String recipientId
    ) {
        if (!sender.equals(user)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return roomService.findByRecipientId(recipientId);
    }

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
