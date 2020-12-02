package org.netcracker.project.model.dto;

import lombok.Builder;
import lombok.Value;
import org.netcracker.project.model.User;

@Value
@Builder
public class SimpleUser {
    Long id;
    String name;
    String surname;
    String username;
    String avatarFilename;

    public static SimpleUser of(User user) {
        return SimpleUser.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .avatarFilename(user.getAvatarFilename())
                .build();
    }
}
