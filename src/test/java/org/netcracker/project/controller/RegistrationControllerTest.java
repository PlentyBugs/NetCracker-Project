package org.netcracker.project.controller;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.User;
import org.netcracker.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void contextLoad(){}

    @Test
    public void registrationPageTest() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(xpath("//div[@class='form-group row']").nodeCount(9))
                .andExpect(xpath("//div[@class='form-group row'][1]").string(containsString("Email")))
                .andExpect(xpath("//div[@class='form-group row'][2]").string(containsString("Name")))
                .andExpect(xpath("//div[@class='form-group row'][3]").string(containsString("Surname")))
                .andExpect(xpath("//div[@class='form-group row'][4]").string(containsString("Second name")))
                .andExpect(xpath("//div[@class='form-group row'][5]").string(containsString("Username")))
                .andExpect(xpath("//div[@class='form-group row'][6]").string(containsString("Password")))
                .andExpect(xpath("//div[@class='form-group row'][7]").string(containsString("Repeat password")))
                .andExpect(xpath("//div[@class='form-group row'][8]").string(containsString("You register as")))
                .andExpect(xpath("//div[@class='form-check form-check-inline'][1]").string(containsString("Participant")))
                .andExpect(xpath("//div[@class='form-check form-check-inline'][2]").string(containsString("Organizer")));
    }

    @Test
    public void registrationSuccessWithRolesTest() throws Exception {
        String email = "mock@bean.com";
        String username = "Big mock";
        mockMvc.perform(
                post("/registration")
                .with(csrf())
                .param("name", "name")
                .param("surname", "surname")
                .param("secName", "secName")
                .param("email", email)
                .param("password", "123456789")
                .param("password2", "123456789")
                .param("username", username)
                .param("role", "PARTICIPANT")
                .param("role", "ORGANIZER")
        ).andExpect(status().isOk());

        verify(userService, times(1)).create(any(User.class), anySet());
    }

    @Test
    public void registrationSuccessWithoutRolesTest() throws Exception {
        String name = "name";
        String surname = "surname";
        String secName = "secName";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "123456789";
        String username = "Big mock";
        ResultMatcher status = status().is3xxRedirection();
        ResultMatcher redirectUrl = redirectedUrl("/login");
        int repositoryInteractionTimes = 1;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, true, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesPasswordsAreDifferentTest() throws Exception {
        String name = "name";
        String surname = "surname";
        String secName = "secName";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "12345678";
        String username = "Big mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 0;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, true, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesUserAlreadyExistsTest() throws Exception {
        String name = "name";
        String surname = "surname";
        String secName = "secName";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "123456789";
        String username = "mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 1;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, false, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesEmptyNameTest() throws Exception {
        String name = "";
        String surname = "surname";
        String secName = "secName";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "12345678";
        String username = "Big mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 0;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, true, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesEmptySurnameTest() throws Exception {
        String name = "name";
        String surname = "";
        String secName = "secName";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "12345678";
        String username = "Big mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 0;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, true, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesEmptySecNameTest() throws Exception {
        String name = "name";
        String surname = "surname";
        String secName = "";
        String email = "mock@bean.com";
        String password = "123456789";
        String password2 = "12345678";
        String username = "Big mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 0;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, false, repositoryInteractionTimes);
    }

    @Test
    public void registrationFailWithoutRolesWrongEmailFormatTest() throws Exception {
        String name = "name";
        String surname = "surname";
        String secName = "secName";
        String email = "mock!bean.com";
        String password = "123456789";
        String password2 = "12345678";
        String username = "Big mock";
        ResultMatcher status = status().isOk();
        ResultMatcher redirectUrl = redirectedUrl(null);
        int repositoryInteractionTimes = 0;
        registrationTest(name, surname, secName, email, password, password2, username, status, redirectUrl, true, repositoryInteractionTimes);
    }

    @Test
    public void activationTest() throws Exception {
        mockMvc.perform(get("/registration/activate/code").with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).activate(anyString());
    }

    private void registrationTest(
            String name,
            String surname,
            String secName,
            String email,
            String password,
            String password2,
            String username,
            ResultMatcher status,
            ResultMatcher redirectUrl,
            boolean returnCreate,
            int repositoryInteractionTimes
    ) throws Exception {
        when(userService.create(any(User.class))).thenReturn(returnCreate);

        mockMvc.perform(
                post("/registration")
                        .with(csrf())
                        .param("name", name)
                        .param("surname", surname)
                        .param("secName", secName)
                        .param("email", email)
                        .param("password", password)
                        .param("password2", password2)
                        .param("username", username)
        ).andExpect(status)
        .andExpect(redirectUrl);

        verify(userService, times(repositoryInteractionTimes)).create(any(User.class));
    }
}
