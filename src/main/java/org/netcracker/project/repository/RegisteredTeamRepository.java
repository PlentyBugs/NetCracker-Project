package org.netcracker.project.repository;

import org.netcracker.project.model.RegisteredTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredTeamRepository extends JpaRepository<RegisteredTeam, Long> {
    RegisteredTeam findByTeamName(String teamName);
}
