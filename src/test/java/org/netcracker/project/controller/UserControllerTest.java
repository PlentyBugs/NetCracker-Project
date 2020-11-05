package org.netcracker.project.controller;


import org.junit.jupiter.api.Test;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@WithUserDetails("steam")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoad() {
        assertNotNull(userController);
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/user/4"))
                .andExpect(authenticated())
                .andExpect(xpath("//img[@id='user-avatar']").exists())
                .andExpect(xpath("//h3[@id='user-full-name']").string(containsString("Жуков Егор Константинович (steam)")))
                .andExpect(xpath("//div[@id='user-team-roles']").string(containsString("Командные роли:")))
                .andExpect(xpath("//li[@class='list-group-item team'][1]/a").string(containsString("Team A")))
                .andExpect(xpath("//li[@class='list-group-item team'][2]/a").string(containsString("FunCo")))
                .andExpect(xpath("//li[@class='list-group-item competition']").nodeCount(2))
                .andExpect(xpath("//li[@class='list-group-item competition'][1]/a").string(containsString("Hackathon 2")))
                .andExpect(xpath("//li[@class='list-group-item competition'][2]/a").string(containsString("Big Data Analysis MegaHackathon Moscow 2021")));
    }

    @Test
    public void updateUserTest() throws Exception {
        mockMvc.perform(put("/user/4")
                .param("name", "name")
                .param("surname", "surname")
                .param("secName", "secName")
                .param("email", "wminecraft616@gmail.com")
                .param("password", "password")
                .param("password2", "password")
                .param("username", "username")
                .with(csrf())
        ).andExpect(status().is3xxRedirection());

        Optional<User> userOptional = userRepository.findById(4L);
        User user;

        assertNotNull((user = userOptional.orElse(null)));

        assertEquals(user.getId(), 4);
        assertEquals(user.getName(), "name");
        assertEquals(user.getSurname(), "surname");
        assertEquals(user.getSecName(), "secName");
        assertEquals(user.getEmail(), "wminecraft616@gmail.com");
        assertTrue(BCrypt.checkpw("password", user.getPassword()));
        assertEquals(user.getUsername(), "username");
    }

    @Test
    public void updateUserFailTest() throws Exception {
        mockMvc.perform(put("/user/3")
                .param("name", "name")
                .param("surname", "surname")
                .param("secName", "secName")
                .param("email", "email")
                .param("password", "password")
                .param("password2", "password")
                .param("username", "username")
                .with(csrf())
        ).andExpect(status().is(403));
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/user/4").param("password2", "admin").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Optional<User> userOptional = userRepository.findById(4L);
        User user;

        assertNotNull((user = userOptional.orElse(null)));
        assertFalse(user.isEnabled());
    }
}
