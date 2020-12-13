package org.netcracker.project.repository;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void findByTeamNameTest() {
        Team team = teamRepository.findByTeamName("Team A");
        assertNotNull(team);
        assertEquals(team.getTeamName(), "Team A");
    }

    @Test
    public void findByTeamNameDoesntExistsTest() {
        Team team = teamRepository.findByTeamName("Team D");
        assertNull(team);
    }

    @Test
    public void findAllTest() {
        Pageable pageable = PageRequest.of(0,10);
        Page<Team> teamPage = teamRepository.findAll(pageable);
        assertNotNull(teamPage);
        assertEquals(teamPage.getTotalElements(), 3);
    }

    @Test
    public void findAllWithFilterMaxTest() {
        Pageable pageable = PageRequest.of(0,10);
        Page<Team> teamPage = teamRepository.findAllWithFilter(pageable, 0, 1, "%");
        assertNotNull(teamPage);
        assertEquals(teamPage.getTotalElements(), 1);
    }

    @Test
    public void findAllWithFilterMinTest() {
        Pageable pageable = PageRequest.of(0,10);
        Page<Team> teamPage = teamRepository.findAllWithFilter(pageable, 2, 50, "%");
        assertNotNull(teamPage);
        assertEquals(teamPage.getTotalElements(), 2);
    }

    @Test
    public void findAllWithFilterNameTest() {
        Pageable pageable = PageRequest.of(0,10);
        Page<Team> teamPage = teamRepository.findAllWithFilter(pageable, 0, 50, "%T%");
        assertNotNull(teamPage);
        assertEquals(teamPage.getTotalElements(), 2);
    }

    @Test
    public void findAllWithFilterAndWithoutMeTest() {
        Pageable pageable = PageRequest.of(0,10);

        User user = new User();
        user.setId(4L);
        user.setActivationCode(null);
        user.setActive(true);
        user.setAvatarFilename("default.png");
        user.setEmail("wminecraft616@gmail.com");
        user.setName("Egor");
        user.setSurname("Zhukov");
        user.setSecName("Konstantinovich");
        user.setPassword("$2a$08$bgSzfgN9UVrXLMzNodznVOerzznIXTMWyD3qBAygUmg507KJ4F5aC");
        user.setUsername("steam");

        Page<Team> teamPage = teamRepository.findAllWithFilterAndWithoutMe(pageable, 0, 50, "%", user);
        assertNotNull(teamPage);
        assertEquals(teamPage.getTotalElements(), 1);
    }
}
