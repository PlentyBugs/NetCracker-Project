package org.netcracker.project.repository;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.RegisteredTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegisteredTeamRepositoryTest {

    @Autowired
    private RegisteredTeamRepository registeredTeamRepository;

    @Test
    public void findByTeamNameTest() {
        RegisteredTeam registeredTeam = registeredTeamRepository.findByTeamName("Team A");
        assertNotNull(registeredTeam);
        assertEquals("Team A", registeredTeam.getTeamName());
    }

    @Test
    public void findByTeamNameDoesntExistsTest() {
        RegisteredTeam registeredTeam = registeredTeamRepository.findByTeamName("Team D");
        assertNull(registeredTeam);
    }
}
