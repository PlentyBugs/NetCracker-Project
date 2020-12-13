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
        assertEquals(user.getUsername(), "steam");
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
        assertEquals(user.get().getId(), 1L);
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
        assertEquals(user.getActivationCode(), "6as1d6asd16as56q19q");
        assertEquals(user.getUsername(), "gog");
        assertFalse(user.isActive());
    }

    @Test
    public void findByActivationCodeDoesntExistsTest() {
        User user = userRepository.findByActivationCode("1asd1as6d1asd16as1d6as1");
        assertNull(user);
    }
}
