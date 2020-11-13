package org.netcracker.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.enums.TeamRole;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
public class Team implements Serializable {
    private static final long serialVersionUID = 7609590770964470381L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String teamName;

    // Надо решить проблему с ленивой подгрузкой
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usr_team",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "usr_id") }
    )
    private Set<User> teammates = new HashSet<>();

    @ElementCollection(targetClass = Result.class, fetch=FetchType.EAGER)
    @CollectionTable(name="result_type",joinColumns = @JoinColumn(name = "team_id"))
    @Enumerated(EnumType.STRING)
    private  Set<Result> result = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "team_competition",
            joinColumns = { @JoinColumn(name = "team_id") },
            inverseJoinColumns = { @JoinColumn(name = "comp_id") }
    )
    @JsonIgnore
    private Set<Competition> competitionHistory = new HashSet<>();

    // Надо решить проблему с ленивой подгрузкой
    @ManyToMany(fetch = FetchType.EAGER)
    @CollectionTable(name = "team_statistics", joinColumns = @JoinColumn(name = "team_id"))
    @MapKeyColumn(name = "result")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Result, Competition> statistics = new HashMap<>();
    //private Set<Competition> statistics = new HashSet<>();

    // Сюда роли добавляются при изменении (добавлении, обновлении) команды через post/put запросы, т.е. это никак не связано с usr_team_role таблицей
    // Так как пользователь, возможно не хочет вступать в команду со всеми его ролями
    @ElementCollection(targetClass = TeamRole.class, fetch=FetchType.EAGER)
    @CollectionTable(name = "team_profession", joinColumns = @JoinColumn(name = "team_id"))
    @Enumerated(EnumType.STRING)
    private Set<TeamRole> professions;

    @Column(name="logo_filename", nullable = false)
    private String logoFilename = "teamLogo.png";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                '}';
    }
}
