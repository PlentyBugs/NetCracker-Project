package org.netcracker.project.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Data
public class Competition {
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
}
