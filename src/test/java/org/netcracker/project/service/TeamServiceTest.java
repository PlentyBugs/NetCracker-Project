package org.netcracker.project.service;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class TeamServiceTest {
    @MockBean
    private TeamRepository teamRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private ImageUtils imageUtils;
    @Test
    public void getPageTest() {

        Pageable pageable = PageRequest.of(0,10);
        Team team = new Team();

        Page<Team> teamPage = new PageImpl<>(Collections.singletonList(team));
        when(teamService.getPage(pageable)).thenReturn(teamPage);

        Page<Team> teams = teamService.getPage(pageable);

        assertNotNull(teams);
        assertEquals(1, teams.getNumberOfElements());
        verify(teamRepository, times(1)).findAll(pageable);
    }


    @Test
    public void saveTest() throws Exception {
        MultipartFile logo = new MockMultipartFile("logo.png","111".getBytes());
        Team team = new Team();
        User user = new User();
        user.setId(3L);

        assertTrue(teamService.save(team, logo, user, Set.of()));
        verify(teamRepository, times(1)).save(team);
    }
    @Test
    public void updateTest() {
        Team team = new Team();
        assertTrue(teamService.update(team));
        verify(teamRepository, times(1)).save(team);
    }

    //filter будет тут

}
