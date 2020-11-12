package org.netcracker.project.repository;

import org.apache.tomcat.jni.Local;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition,Long> {
    Competition findByCompName(String compName);

    Page<Competition> findAll(Pageable pageable);

    @Query("from Competition c where lower(c.description) like lower(:search) or lower(c.compName) like lower(:search)")
    Page<Competition> findAllBySearch(Pageable pageable, String search);

    Page<Competition> findAllByStartDateAfter(Pageable pageable, LocalDateTime startDate);

    Page<Competition> findAllByStartDateBefore(Pageable pageable, LocalDateTime startDate);

    @Query("from Competition  c where c.endDate < :endDate and (c.organizer = :user)")
    Page<Competition> getArchiveByUser(Pageable pageable, LocalDateTime endDate, User user);

    @Query("from Competition  c where c.endDate >= :endDate and (c.organizer = :user)")
    Page<Competition> getRunningCompByUser(Pageable pageable, LocalDateTime endDate,User user);

    @Query("from Competition c where c.startDate = :startDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByStartDateEquals(Pageable pageable, LocalDateTime startDate, String search);

    @Query("from Competition c where c.endDate = :endDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByEndDateEquals(Pageable pageable, LocalDateTime endDate, String search);

    @Query("from Competition c where c.startDate = :startDate and c.endDate = :endDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByStartDateEqualsAndEndDateEquals(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, String search);

    @Query("from Competition c where c.startDate <= :beforeStart and c.startDate >= :afterStart and c.endDate <= :beforeEnd and c.endDate >= :afterEnd and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByBounds(Pageable pageable, LocalDateTime beforeStart, LocalDateTime afterStart, LocalDateTime beforeEnd, LocalDateTime afterEnd, String search);

    @Query("from Competition c where :team member of c.teams")
    List<Competition> findAllByTeam(Team team);

    @Query("from Competition c where exists (select t.id from c.teams as t where :teamId = t.id) and c.startDate >= :startDate and c.startDate <= :endMonthDate")
    List<Competition> findAllByTeamCalendar(Long teamId, LocalDateTime startDate, LocalDateTime endMonthDate);

    @Query("from Competition c where exists (select t.id from Team as t where :user member of t.teammates)")
    List<Competition> findAllByUser(User user);

    @Query("from Competition c where exists (select t.id from Team as t where :user member of t.teammates) and c.startDate >= :startDate and c.startDate <= :endMonthDate")
    List<Competition> findAllByUserCalendar(User user, LocalDateTime startDate, LocalDateTime endMonthDate);

    @Query("from Competition c where c.startDate >= :startDate and c.startDate <= :endMonthDate")
    List<Competition> findAllCalendar(LocalDateTime startDate, LocalDateTime endMonthDate);
}
