package org.netcracker.project.repository;

import org.netcracker.project.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team,Long> {
    Team findByTeamName(String teamName);

   Page<Team> findAll(Pageable pageable);
}
