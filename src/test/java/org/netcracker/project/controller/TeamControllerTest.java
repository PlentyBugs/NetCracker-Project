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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.containsString;
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
    private MockMvc mockMvc;

    @Test
    public void getAllTeamsTest() throws Exception {
        mockMvc.perform(get("/team"))
                .andExpect(authenticated())
                .andExpect(xpath("//img[@class='user-image-sm']").nodeCount(3));
    }
    @Test
    public void addTeamTest() throws Exception {
        MockHttpServletRequestBuilder multipart=multipart("/team")
                .file("logo","teamLogo".getBytes())
                .param("teamName","Team A 2")
                .with(csrf());

        assertEquals(teamRepository.findAll().size(),3);

        mockMvc.perform((multipart))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team"));

        assertEquals(teamRepository.findAll().size(),4);
        Team team=teamRepository.findById(10L).orElse(new Team());
        assertEquals(team.getLogoFilename(),"teamLogo.png");
        assertEquals(team.getTeamName(),"Team A 2");
    }

    @Test
    public  void addTeamFailTeamNameTest() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/team")
                .file("logo", "teamLogo".getBytes())
                .with(csrf());

        mockMvc.perform(multipart)
                .andExpect(authenticated());
    }
    @Test
    public void joinTeamTest()throws Exception{
        mockMvc.perform(get("/team/2"))
                .andExpect(authenticated())
                .andExpect(xpath("//img[@id='team-logo']").exists())
                .andExpect(xpath("//h2[@id='team-name-header']").string(containsString("Train B")))
                .andExpect(xpath("//button[@class='btn btn-success btn-lg btn-block']").exists());
        mockMvc.perform(post("/team/2/join").with(csrf()))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team/2"));
    }
    @Test
    public void quitTeamTest() throws Exception{
        mockMvc.perform(get("/team/3"))
                .andExpect(xpath("//button[@class='btn btn-warning btn-lg btn-block']").exists());
        mockMvc.perform(post("/team/3/quit").with(csrf()))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/team/3"));
    }

    @Test
    public void getTeamTest() throws  Exception{
        mockMvc.perform(get("/team/3"))
                .andExpect(authenticated())
                .andExpect(xpath("//h2[@id='team-name-header']").string(containsString("FunCo")))
                .andExpect(xpath("//img[@id='team-logo']").exists());
    }

    @Test
    public void updateAvatarTest() throws Exception{
        MockHttpServletRequestBuilder multipart = multipart("/team/3/image")
                .file("avatar","teamLogo".getBytes())
                .with(csrf());
        mockMvc.perform(multipart)
                .andExpect(authenticated());
        Team team=teamRepository.findById(3L).get();
        assertEquals(team.getLogoFilename(),"teamLogo.png");
    }
    @Test
    public void inviteUserTest() throws Exception{
       // mockMvc.perform(put("/team/3/invite/2"))
               //
    }
}
