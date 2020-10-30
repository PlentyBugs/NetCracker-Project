package org.netcracker.project.repository;

import org.netcracker.project.model.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CompetitionRepository extends JpaRepository<Competition,Long> {
    Competition findByCompName(String compName);
    // @Query(...)
    Competition queryByStartDate(LocalDateTime startDate);

    Page<Competition> findAll(Pageable pageable);

    Page<Competition> findAllByStartDateAfter(Pageable pageable, LocalDateTime startDate);

    Page<Competition> findAllByStartDateBefore(Pageable pageable, LocalDateTime startDate);

    Page<Competition> findAllByStartDateEquals(Pageable pageable, LocalDateTime startDate);
}
