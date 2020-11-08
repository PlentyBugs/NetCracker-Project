package org.netcracker.project.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamFilter {
    Boolean alreadyInTheGroup;
    Boolean removeEmpty;
    Boolean minMembersOn;
    Boolean maxMembersOn;
    Integer minMembers;
    Integer maxMembers;
    String searchName;

    public void validate() {
        alreadyInTheGroup = (alreadyInTheGroup) != null && alreadyInTheGroup;
        removeEmpty = (removeEmpty) != null && removeEmpty;
        minMembersOn = (minMembersOn) != null && minMembersOn;
        maxMembersOn = (maxMembersOn) != null && maxMembersOn;
        minMembers = (minMembers) == null ? 0 : minMembers;
        maxMembers = (maxMembers) == null ? 10000 : maxMembers;
        searchName = (searchName) == null ? "" : searchName;
    }

    public String getFormattedSearchName() {
        return "%" + searchName + "%";
    }
}
