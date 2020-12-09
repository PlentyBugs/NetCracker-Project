package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final RoomService roomService;

    /**
     * Принимает GET запросы на url: URL/messenger
     * Используется для генерации страницы мессенджера
     * @param user Пользователь, совершивший запрос
     * @param model Объект Model, в который будут помещены переменные для генерации страницы.
     * @return Страница мессенджера
     */
    @GetMapping("/messenger")
    public String getPage(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        roomService.getPage(model, user);
        return "messenger";
    }

    /**
     * Принимает GET запросы на url: URL/messenger/{recipientId}
     * Используется для генерации страницы мессенджера
     * Также, в отличие от метода без recipientId, позволяет сгенерировать страницу
     * с начальным чатом.
     * На сервере этот id не используется и нужен лишь как заглушка, все действия происходят на клиенте
     * @param user Пользователь, совершивший запрос
     * @param model Объект Model, в который будут помещены переменные для генерации страницы.
     * @param recipientId Id пользователя, с чатом которого будет открыта страница мессенджера
     * @return Страница мессенджера с чатом пользователя, чей id был в url
     */
    @GetMapping("/messenger/{recipientId}")
    public String getPageWithRecipient(
            @AuthenticationPrincipal User user,
            Model model,
            @PathVariable(value = "recipientId") String recipientId
    ) {
        roomService.getPage(model, user);
        return "messenger";
    }

    /**
     * Принимает GET запросы на url: URL/messenger/{userId}/chat/personal/{chatId}
     * Используется для получения определенного чата
     * В url:
     * userId - Id пользователя, который предположительно делает запрос
     * chatId - Id чата, который запрашивается пользователем
     * @param user Пользователь, совершивший запрос
     * @param senderId Id пользователя, который предположительно делает запрос и является участником приватного чата
     * @param chatId Id чата, который запрашивается пользователем
     * @return Объект Room - чат, запрос на который был получен
     */
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

    /**
     * Принимает GET запросы на url: URL/messenger/{userId}/chat/group/{chatId}
     * Используется для получения определенного группового чата
     * В url:
     * userId - Id пользователя, который предположительно делает запрос
     * chatId - Id группового чата, который запрашивается пользователем
     * @param user Пользователь, совершивший запрос
     * @param senderId Id пользователя, который предположительно делает запрос и является участником группового чата
     * @param chatId Id группового чата, который запрашивается пользователем
     * @return Объект GrouзRoom - групповой чат, запрос на который был получен
     */
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

    /**
     * Принимает POST запросы на url: URL/messenger/{userId}/chat/group
     * Используется для создания группового чата пользователем
     * С определенным создателем, названием группового чата и набором начальных участников
     * @param user Пользователь, совершивший запрос
     * @param senderId Id пользователя, который будет считаться создателем группового чата
     * @param chatName Строка, содержащая название группового чата
     * @param userIds Множество строк, содержащих Id пользователей, которые будут участниками группового чата на момент его создания
     */
    @PostMapping(value = "/messenger/{userId}/chat/group")
    @ResponseBody
    public void createGroupChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String senderId,
            @RequestParam("chatName") String chatName,
            @RequestBody Set<String> userIds
    ) {
        if (!String.valueOf(user.getId()).equals(senderId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        userIds.add(senderId);
        roomService.createGroupRoom(senderId, userIds, chatName.substring(1, chatName.length() - 1));
    }

    /**
     * Принимает POST запросы на url: URL/messenger/{userId}/chat/group/{chatId}/kick/{recipientId}
     * Используется для изгнания пользователя из группового чата его администратором
     * @param user Пользователь, совершивший запрос
     * @param adminId Id администратора группового чата, который изгоняет пользователя
     * @param chatId Id группового чата из которого изгоняется пользователь
     * @param recipientId Id пользователя, изгоняемого из группового чата
     */
    @PostMapping(value = "/messenger/{userId}/chat/group/{chatId}/kick/{recipientId}", produces = "application/json")
    @ResponseBody
    public void kickFromGroupChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String adminId,
            @PathVariable("chatId") String chatId,
            @PathVariable("recipientId") String recipientId
    ) {
        if (!String.valueOf(user.getId()).equals(adminId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        roomService.removeGroupMemberWithAuthCheck(chatId, recipientId, adminId);
    }

    /**
     * Принимает PUT запросы на url: URL/messenger/{userId}/chat/group/{chatId}/participant
     * Используется для добавления новых пользователей в групповой чат
     * @param user Пользователь, совершивший запрос
     * @param userId Id пользователя-участника группового чата
     * @param chatId Id группового чата в который приглашаются пользователи
     * @param newUserIds Множество строк, содержащих Id приглашенных пользователей
     */
    @PutMapping(value = "/messenger/{userId}/chat/group/{chatId}/participant")
    @ResponseBody
    public void addUsersToGroupChat(
            @AuthenticationPrincipal User user,
            @PathVariable("userId") String userId,
            @PathVariable("chatId") String chatId,
            @RequestBody Set<String> newUserIds
    ) {
        if (!String.valueOf(user.getId()).equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        roomService.addGroupMembers(chatId, newUserIds);
    }

    /**
     * Принимает DELETE запросы на url: URL/messenger/{adminId}/chat/group/{chatId}/participant/{userId}
     * Используется для того, чтобы пользователь мог покинуть групповой чат
     * @param user Пользователь, совершивший запрос
     * @param adminId Id пользователя, являющегося админом группового чата
     * @param chatId Id группового чата, который покидает пользователь
     * @param leavingUserId Id пользователя, который покидает групповой чат
     */
    @DeleteMapping(value = "/messenger/{adminId}/chat/group/{chatId}/participant/{userId}")
    @ResponseBody
    public void leaveGroupChat(
            @AuthenticationPrincipal User user,
            @PathVariable("adminId") String adminId,
            @PathVariable("chatId") String chatId,
            @PathVariable("userId") String leavingUserId
    ) {
        if (!String.valueOf(user.getId()).equals(leavingUserId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        roomService.removeGroupMemberWithAuthCheck(chatId, leavingUserId, adminId);
    }

    /**
     * Принимает POST запросы на url: URL/messenger/{userId}/chat/personal/{recipientId}
     * Используется для создания нового приватного чата
     * @param user Пользователь, совершивший запрос
     * @param senderId Id первого участника приватного чата и человек, который предположительно совершает запрос на создание
     * @param recipientId Id второго участника приватного чата
     */
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

    /**
     * Принимает GET запросы на url: URL/messenger/messages/{chatId}
     * Используется для получения списка сообщений по чату
     * @param user Пользователь, совершивший запрос
     * @param chatId Id чата, сообщения которого запрашиваются
     * @return Список сообщений данного чата
     */
    @GetMapping(value = "/messenger/messages/{chatId}", produces = "application/json")
    @ResponseBody
    public List<Message> getMessages(
            @AuthenticationPrincipal User user,
            @PathVariable("chatId") String chatId
    ) {
        return messageService.findByChatId(chatId);
    }

    /**
     * Принимает GET запросы на url: URL/messenger/users/group/{chatId}
     * Используется для получения списка участников группового чата по данному Id чата
     * @param user Пользователь, совершивший запрос
     * @param chatId Id группового чата, пользователи которого запрашиваются
     * @return Множество пользователей-участников группового чата в DTO SimpleUser
     */
    @GetMapping(value = "/messenger/users/group/{chatId}", produces = "application/json")
    @ResponseBody
    public Set<SimpleUser> getParticipantsGroup(
            @AuthenticationPrincipal User user,
            @PathVariable("chatId") String chatId
    ) {
        return roomService.getSimpleParticipantsGroup(chatId, user);
    }

    /**
     * Принимает GET запросы на url: URL/messenger/users/personal/{chatId}
     * Используется для получения списка участников приватного чата по данному Id чата
     * @param user Пользователь, совершивший запрос
     * @param chatId Id приватного чата, пользователи которого запрашиваются
     * @return Множество пользователей-участников приватного чата в DTO SimpleUser
     */
    @GetMapping(value = "/messenger/users/personal/{chatId}", produces = "application/json")
    @ResponseBody
    public Set<SimpleUser> getParticipantsPersonal(
            @AuthenticationPrincipal User user,
            @PathVariable("chatId") String chatId
    ) {
        return roomService.getSimpleParticipants(chatId, user);
    }

    /**
     * Метод, который принимает сообщения на /chat
     * Если чата, в который отправляется сообщение, то он создается
     * После сохранения сообщения идет рассылка отправителю и получателю
     * А также отправляются оповещения о новом сообщении
     * @param message Сообщение, которое будет обработано и отправлено получателю и отправителю
     */
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

    /**
     * Метод, который принимает сообщения на /chat/{chatId}
     * Метод отправляет сообщение всем участникам чата
     * А также отправляет уведомления о новом сообщении
     * @param message Сообщение, которое будет отправлено всем участникам чата
     * @param chatId Id чата, в который было отправлено сообщение
     */
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
