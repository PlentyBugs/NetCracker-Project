package org.netcracker.project.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.ChatStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotification {
    private String chatId;
    private String chatName;
    private String recipientId;
    private ChatStatus status;
    private boolean group;
}
