package org.netcracker.project.repository;

import org.netcracker.project.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CompetitionRepository extends JpaRepository<Competition,Long> {
    Competition findByCompName(String compName);
    // @Query(...)
    Competition queryByStartDate(LocalDateTime startDate);
}
