package org.netcracker.project.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
    private User organizer;

    // Надо решить проблему с ленивой подгрузкой
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "statistics",
            joinColumns = { @JoinColumn(name = "comp_id") },
            inverseJoinColumns = { @JoinColumn(name = "team_id") }
    )
    private Set<Team> teams = new HashSet<>();

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
