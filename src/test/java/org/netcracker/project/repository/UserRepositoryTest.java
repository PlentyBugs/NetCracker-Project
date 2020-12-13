package org.netcracker.project.repository;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByUsernameTest() {
        User user = userRepository.findByUsername("steam");
        assertNotNull(user);
        assertEquals("steam", user.getUsername());
    }

    @Test
    public void findByUsernameDoesntExistsTest() {
        User user = userRepository.findByUsername("Santa");
        assertNull(user);
    }

    @Test
    public void findByIdTest() {
        Optional<User> user = userRepository.findById(1L);
        assertTrue(user.isPresent());
        assertEquals(1L, user.get().getId());
    }

    @Test
    public void findByIdDoesntExistsTest() {
        Optional<User> user = userRepository.findById(100L);
        assertTrue(user.isEmpty());
    }

    @Test
    public void findByActivationCodeTest() {
        User user = userRepository.findByActivationCode("6as1d6asd16as56q19q");
        assertNotNull(user);
        assertEquals("6as1d6asd16as56q19q", user.getActivationCode());
        assertEquals("gog", user.getUsername());
        assertFalse(user.isActive());
    }

    @Test
    public void findByActivationCodeDoesntExistsTest() {
        User user = userRepository.findByActivationCode("1asd1as6d1asd16as1d6as1");
        assertNull(user);
    }
}
