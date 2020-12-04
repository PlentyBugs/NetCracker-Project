package org.netcracker.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.netcracker.project.model.embeddable.Statistics;
import org.netcracker.project.model.embeddable.UserTeamRole;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.interfaces.Statistical;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Team implements Serializable, Statistical {
    private static final long serialVersionUID = 7609590770964470381L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "usr_id")
    @JsonIgnore
    private User organizer;

    private String groupChatId;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "team_statistics",
            joinColumns = @JoinColumn(name = "statfk")
    )
    private Set<Statistics> statistics = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "team_user_team_role",
            joinColumns = @JoinColumn(name = "userteamrolefk")
    )
    private Set<UserTeamRole> userTeamRoles = new HashSet<>();

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
