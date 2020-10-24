package org.netcracker.project.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,USER;  //maybe will be more

    @Override
    public String getAuthority() {
        return name();
    }
}
