package org.netcracker.project.repository;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team,Long> {
    Team findByTeamName(String teamName);

    Page<Team> findAll(Pageable pageable);

    @Query("from Team t where size(t.teammates) >= :min and size(t.teammates) <= :max and lower(t.teamName) like lower(:name) and not :user member of t.teammates")
    Page<Team> findAllWithFilterAndWithoutMe(Pageable pageable, int min, int max, String name, User user);

    @Query("from Team t where size(t.teammates) >= :min and size(t.teammates) <= :max and lower(t.teamName) like lower(:name) ")
    Page<Team> findAllWithFilter(Pageable pageable, int min, int max, String name);
}
