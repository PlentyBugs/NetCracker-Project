package org.netcracker.project.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageNotification {
    private String id;
    private Long senderId;
    private Long senderName;
}
