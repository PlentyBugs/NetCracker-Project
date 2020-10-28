package org.netcracker.project.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime endDate;
}
