package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.messaging.GroupRoom;
import org.netcracker.project.model.messaging.Message;
import org.netcracker.project.model.messaging.MessageNotification;
import org.netcracker.project.model.messaging.Room;
import org.netcracker.project.service.MessageService;
import org.netcracker.project.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
        roomService.getPage(model, user);
        return "messenger";
    }

    @GetMapping("/messenger/{recipientId}")
    public String getPageWithRecipient(
            @AuthenticationPrincipal User user,
            Model model,
            @PathVariable(value = "recipientId") String recipientId
    ) {
        roomService.getPage(model, user);
        return "messenger";
    }

    @GetMapping(value = "/messenger/{userId}/chat/personal/{chatId}", produces = "application/json")
    @ResponseBody
    public Room getChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String senderId,
            @PathVariable("chatId") String chatId
    ) {
        if (!String.valueOf(user.getId()).equals(senderId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return roomService.findRoomByChatId(chatId);
    }

    @GetMapping(value = "/messenger/{userId}/chat/group/{chatId}", produces = "application/json")
    @ResponseBody
    public GroupRoom getGroupChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String senderId,
            @PathVariable("chatId") String chatId
    ) {
        if (!String.valueOf(user.getId()).equals(senderId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return roomService.findGroupRoomByChatId(chatId);
    }

    @PostMapping(value = "/messenger/{userId}/chat/personal/{recipientId}")
    @ResponseBody
    public void createChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String senderId,
            @PathVariable("recipientId") String recipientId
    ) {
        if (!String.valueOf(user.getId()).equals(senderId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        roomService.createRoom(senderId, recipientId);
    }

    @GetMapping(value = "/messenger/messages/{chatId}", produces = "application/json")
    @ResponseBody
    public List<Message> getMessages(
            @AuthenticationPrincipal User user,
            @PathVariable("chatId") String chatId
    ) {
        return messageService.findByChatId(chatId);
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        Optional<String> chatId = roomService.getRoomId(message.getSenderId(), message.getRecipientId(), true);
        message.setChatId(chatId.get());

        Message savedMessage = messageService.save(message);

        MessageNotification notification = new MessageNotification(
                savedMessage.getId(),
                savedMessage.getChatId(),
                savedMessage.getSenderId(),
                savedMessage.getRecipientId()
        );

        messagingTemplate.convertAndSendToUser(
                message.getSenderId(), "/queue/messages",
                notification
        );
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages",
                notification
        );
    }

    @MessageMapping("/chat/{chatId}")
    public void processGroupMessage(@Payload Message message, @DestinationVariable String chatId) {
        message.setChatId(chatId);

        Message savedMessage = messageService.save(message);

        GroupRoom groupRoom = roomService.findGroupRoomByChatId(chatId);

        for (String recipientId : groupRoom.getParticipantIds()) {
            messagingTemplate.convertAndSendToUser(
                    recipientId, "/queue/messages",
                    new MessageNotification(
                            savedMessage.getId(),
                            savedMessage.getChatId(),
                            savedMessage.getSenderId(),
                            savedMessage.getRecipientId()
                    )
            );
        }
    }
}
