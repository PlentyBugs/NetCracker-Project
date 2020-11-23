package org.netcracker.project.model.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class GroupRoom {
    @Id
    private String id;
    private String chatId;
    private String chatName;
    private String adminId;
    private Set<String> participantIds;
}
