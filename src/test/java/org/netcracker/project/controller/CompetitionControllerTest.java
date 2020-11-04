package org.netcracker.project.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("steam")
public class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompetitionController competitionController;

    @Test
    public void contextLoad() {
        assertNotNull(competitionController);
    }

    @Test
    public void competitionListCheck() throws Exception {
        mockMvc.perform(get("/competition"))
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='competition-list']/div/div/a").nodeCount(2));
    }

    @Test
    public void specificCompetitionInListTest() throws Exception {
        mockMvc.perform(get("/competition"))
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/a/img").exists())
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/div/h5").string(containsString("Big Data Analysis MegaHackathon Moscow 2021")))
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/div/p[1]").string(containsString("Participate and be a part of history, let megacorporations spot you and reserve a spot for you")))
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/div/a").string(containsString("Go to competition")))
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/div/p[2]/small[1]").string(containsString("2021-01-12 17:55")))
                .andExpect(xpath("//div[@id='competition-list']/div[1]/div/div/p[2]/small[2]").string(containsString("2021-09-12 18:15")));
    }

    @Test
    public void specificCompetitionTest() throws Exception {
        mockMvc.perform(get("/competition/2"))
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='main']/div/div/img").exists())
                .andExpect(xpath("//div[@id='main']/div/div/div[1]/h1").string(containsString("Big Data Analysis MegaHackathon Moscow 2021")))
                .andExpect(xpath("//div[@id='main']/div/div/div[1]/h6").string(containsString("Organized by Егор")))
                .andExpect(xpath("//div[@id='main']/div/div/div[1]/p[1]").string(containsString("Participate and be a part of history, let megacorporations spot you and reserve a spot for you")))
                .andExpect(xpath("//div[@id='main']/div/div/div[1]/p[2]").string(containsString("2021-01-12 17:55")))
                .andExpect(xpath("//div[@id='main']/div/div/div[1]/p[3]").string(containsString("2021-09-12 18:15")));
    }
}
