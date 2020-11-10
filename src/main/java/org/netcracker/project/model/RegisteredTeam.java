package org.netcracker.project.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredTeam implements Serializable {
    @Id
    private Long id;
    @Column(unique = true)
    private String teamName;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usr_registered_team",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "usr_id") }
    )
    private Set<User> teammates = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredTeam team = (RegisteredTeam) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Registered Team{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                '}';
    }

    public static RegisteredTeam of(Team team) {
        RegisteredTeamBuilder builder = new RegisteredTeamBuilder();
         return builder
                .id(team.getId())
                .teamName(team.getTeamName())
                .teammates(team.getTeammates())
                .build();
    }
}
