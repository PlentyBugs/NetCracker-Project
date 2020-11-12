package org.netcracker.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.netcracker.project.model.enums.Theme;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class Competition implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Competition's name can't be empty!")
    private String compName;
    @NotBlank(message = "Competition's description can't be empty!")
    private String description;
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime endDate;
    @Column(name="title_filename", nullable = false)
    private String titleFilename = "compTitle.png";
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usr_id")
    @JsonIgnore
    private User organizer;

    // Надо решить проблему с ленивой подгрузкой
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "statistics",
            joinColumns = { @JoinColumn(name = "comp_id") },
            inverseJoinColumns = { @JoinColumn(name = "team_id") }
    )
    @JsonIgnore
    private Set<RegisteredTeam> teams = new HashSet<>();

    private boolean CompEnded;  //флажок, который можно ставить по окончанию соревнования, чтобы удалять его.

    @Min(0)
    private Long prizeFund;

    @ElementCollection(targetClass = Theme.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "competition_theme", joinColumns = @JoinColumn(name = "comp_id"))
    @Enumerated(EnumType.STRING)
    private Set<Theme> themes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competition that = (Competition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", compName='" + compName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
