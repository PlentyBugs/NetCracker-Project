package org.netcracker.project.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SimpleUser {
    Long id;
    String name;
    String surname;
    String username;
}
