package org.netcracker.project.service;


import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private MailService mailService;

    @Test
    public void createTest() {
        User user = new User();

        user.setEmail("mock@gmail.com");
        user.setPassword("123");

        assertTrue(userService.create(user));
        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(mailService, Mockito.times(1)).send(
                ArgumentMatchers.eq(user.getEmail()),
                ArgumentMatchers.endsWith("Activation Code"),
                ArgumentMatchers.contains("Welcome to NetHacker")
        );
    }

    @Test
    public void createFailTest() {
        User user = new User();

        user.setUsername("Mock");

        Mockito.doReturn(new User()).when(userRepository).findByUsername("Mock");

        assertFalse(userService.create(user));

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailService, Mockito.times(0))
                .send(ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString());
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode("netchacker");
        Mockito.doReturn(user).when(userRepository).findByActivationCode("activate");

        boolean isUserActivated = userService.activate("activate");

        assertTrue(isUserActivated);
        assertNull(user.getActivationCode());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFalse() {
        assertFalse(userService.activate("activate me"));
        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}
