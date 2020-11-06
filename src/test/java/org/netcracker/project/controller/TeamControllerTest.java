package org.netcracker.project.controller;


import org.junit.jupiter.api.Test;
import org.netcracker.project.model.Team;
import org.netcracker.project.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("steam")
public class TeamControllerTest {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamController teamController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllTeamsTest() throws Exception {
        mockMvc.perform(get("/team"))
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='team-list']").string(containsString("Команды:")))
                .andExpect(xpath("//button[@class='btn btn-warning btn-lg btn-block']").exists());
    }
    @Test
    public void addTeamTest() throws Exception {
        MockHttpServletRequestBuilder multipart=multipart("/team")
                .file("logo","teamLogo".getBytes())
                .param("teamName","Team A")
                .with(csrf());

        assertEquals(teamRepository.findAll().size(),3);

        mockMvc.perform((multipart))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team"));

        assertEquals(teamRepository.findAll().size(),4);
        Team team=teamRepository.findById(10L).orElse(new Team());
        assertEquals(team.getLogoFilename(),"teamLogo");
        assertEquals(team.getTeamName(),"Team A");
    }

    @Test
    public  void addTeamFailTeamNameTest() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/team")
                .file("logo", "teamLogo".getBytes())
                .with(csrf());

        mockMvc.perform(multipart)
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/add-team"));
    }
    @Test
    public void joinTeamTest()throws Exception{
        mockMvc.perform(get("/team/2"))
                .andExpect(authenticated())
                .andExpect(xpath("//img[@id='team-logo']").exists())
                .andExpect(xpath("//div[@id='main']/div/div[1]/h1").string(containsString("Train B")))
                .andExpect(xpath("//button[@class='btn btn-success btn-lg btn-block']").exists());
        mockMvc.perform(post("/team/2/join").with(csrf()))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team/2"));
    }
    @Test
    public void quitTeamTest() throws Exception{
        mockMvc.perform(get("/team/2"))
                .andExpect(xpath("//button[@class='btn btn-warning btn-lg btn-block']").exists());
        mockMvc.perform(post("/team/2/quit").with(csrf()))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team/2"));
    }
}

