package org.netcracker.project.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamFilter {
    boolean notInTheGroup;
    boolean removeEmpty;
    boolean minMembersOn;
    boolean maxMembersOn;
    Integer minMembers;
    Integer maxMembers;
    String searchName;
}
