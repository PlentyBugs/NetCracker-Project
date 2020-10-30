package org.netcracker.project.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String teamName;

    @ManyToMany
    @JoinTable(
            name = "usr_team",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "usr_id") }
    )
    private Set<Team> teammates = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "statistics",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "comp_id") }
    )
    private Set<Competition> statistics = new HashSet<>();
}
