package org.netcracker.project.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, USER, PARTICIPANT, ORGANIZER;  //maybe will be more

    @Override
    public String getAuthority() {
        return name();
    }
}
