package org.netcracker.project.model.dto;

import lombok.Builder;
import lombok.Value;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Builder
public class SimpleTeam {
    Long id;
    Set<Long> teammatesId;
    String teamName;

    public static SimpleTeam of(Team team) {
        return SimpleTeam.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .teammatesId(team.getTeammates().stream().map(User::getId).collect(Collectors.toSet()))
                .build();
    }
}
