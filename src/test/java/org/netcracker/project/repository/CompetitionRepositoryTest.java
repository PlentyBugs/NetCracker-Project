package org.netcracker.project.repository;

import org.junit.jupiter.api.Test;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create-user-before.sql", "/create-competition-before.sql", "/create-team-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/create-team-after.sql", "/create-competition-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;
    private final Pageable pageable = PageRequest.of(0,10);

    @Test
    public void findByCompNameTest() {
        Competition competition = competitionRepository.findByCompName("Hackathon");
        assertNotNull(competition);
        assertEquals("Hackathon", competition.getCompName());
    }

    @Test
    public void findByCompNameDoesntExistsTest() {
        Competition competition = competitionRepository.findByCompName("Defunct Hackathon");
        assertNull(competition);
    }

    @Test
    public void findAllTest() {
        Page<Competition> competitionPage = competitionRepository.findAll(pageable);
        assertNotNull(competitionPage);
        assertEquals(5, competitionPage.getTotalElements());
    }

    @Test
    public void findAllBySearch() {
        Page<Competition> competitionPage = competitionRepository.findAllBySearch(pageable, "%2%");
        assertNotNull(competitionPage);
        assertEquals(2, competitionPage.getTotalElements());
    }

    @Test
    public void findAllByStartDateAfterTest() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        Page<Competition> competitionPage = competitionRepository.findAllByStartDateAfter(pageable, dateTime);
        assertNotNull(competitionPage);
        assertEquals(2, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getStartDate().isAfter(dateTime));
        }
    }

    @Test
    public void findAllByStartDateBeforeTest() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0);
        Page<Competition> competitionPage = competitionRepository.findAllByStartDateBefore(pageable, dateTime);
        assertNotNull(competitionPage);
        assertEquals(3, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getStartDate().isBefore(dateTime));
        }
    }

    @Test
    public void findAllByStartDateEqualsWithAnySearchTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByStartDateEquals(pageable, dateTime, "%");
        assertNotNull(competitionPage);
        assertEquals(2, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getStartDate().isEqual(dateTime));
        }
    }

    @Test
    public void findAllByStartDateEqualsWithSearchTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 1, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByStartDateEquals(pageable, dateTime, "%Hackathon 3%");
        assertNotNull(competitionPage);
        assertEquals(1, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getStartDate().isEqual(dateTime));
        }
    }

    @Test
    public void findAllByEndDateEqualsWithAnySearchTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 3, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByEndDateEquals(pageable, dateTime, "%");
        assertNotNull(competitionPage);
        assertEquals(1, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getEndDate().isEqual(dateTime));
        }
    }

    @Test
    public void findAllByEndDateEqualsWithSearchTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 3, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByEndDateEquals(pageable, dateTime, "%Hackathon%");
        assertNotNull(competitionPage);
        assertEquals(1, competitionPage.getTotalElements());
        for (Competition competition : competitionPage.getContent()) {
            assertTrue(competition.getEndDate().isEqual(dateTime));
        }
    }

    @Test
    public void findAllByEndDateEqualsWithSearchNotFoundTest() {
        LocalDateTime dateTime = LocalDateTime.of(2020, 3, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByEndDateEquals(pageable, dateTime, "%Hackathon 5%");
        assertNotNull(competitionPage);
        assertEquals(0, competitionPage.getTotalElements());
    }

    @Test
    public void findAllByStartDateEqualsAndEndDateEqualsTest() {
        LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 12, 12, 45);
        LocalDateTime endDateTime = LocalDateTime.of(2020, 3, 12, 12, 45);
        Page<Competition> competitionPage = competitionRepository.findAllByStartDateEqualsAndEndDateEquals(pageable, startDateTime, endDateTime, "%");
        assertNotNull(competitionPage);
        assertEquals(1, competitionPage.getTotalElements());
    }

    @Test
    public void findAllByBoundsTest() {
        LocalDateTime beforeStart = LocalDateTime.of(2020, 7, 12, 0, 0);
        LocalDateTime afterStart = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime beforeEnd = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime afterEnd = LocalDateTime.of(2020, 2, 1, 0, 0);
        Page<Competition> competitionPage = competitionRepository.findAllByBounds(
                pageable,
                beforeStart,
                afterStart,
                beforeEnd,
                afterEnd,
                "%"
        );
        assertNotNull(competitionPage);
        assertEquals(3, competitionPage.getTotalElements());
    }

    @Test
    public void findAllByTeamCalendarTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 2, 1, 0, 0);
        List<Competition> competitions = competitionRepository.findAllByTeamCalendar(2L, startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(2, competitions.size());
    }

    @Test
    public void findAllByTeamCalendarNotFoundTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 2, 1, 0, 0);
        List<Competition> competitions = competitionRepository.findAllByTeamCalendar(3L, startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(0, competitions.size());
    }

    @Test
    public void findAllByUserCalendarTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 2, 1, 0, 0);

        User user = new User();
        user.setId(1L);
        user.setActivationCode(null);
        user.setActive(true);
        user.setAvatarFilename("default.png");
        user.setEmail("wminecraft616@gmail.com");
        user.setName("Goga");
        user.setSurname("Zhukov");
        user.setSecName("Konstantinovich");
        user.setPassword("$2a$08$bgSzfgN9UVrXLMzNodznVOerzznIXTMWyD3qBAygUmg507KJ4F5aC");
        user.setUsername("mock");

        List<Competition> competitions = competitionRepository.findAllByUserCalendar(user, startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(2, competitions.size());
    }

    @Test
    public void findAllByUserCalendarNotFoundTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 2, 1, 0, 0);

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

        List<Competition> competitions = competitionRepository.findAllByUserCalendar(user, startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(0, competitions.size());
    }

    @Test
    public void findAllCalendarTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 2, 1, 0, 0);
        List<Competition> competitions = competitionRepository.findAllCalendar(startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(2, competitions.size());
    }

    @Test
    public void findAllCalendarNotFoundTest() {
        LocalDateTime startDate = LocalDateTime.of(2020, 10, 1, 0, 0);
        LocalDateTime endMonthDate = LocalDateTime.of(2020, 11, 1, 0, 0);
        List<Competition> competitions = competitionRepository.findAllCalendar(startDate, endMonthDate);
        assertNotNull(competitions);
        assertEquals(0, competitions.size());
    }

    @Test
    public void getArchiveByUserTest() {
        LocalDateTime endDate = LocalDateTime.of(2020, 5, 1, 0, 0);
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
        List<Competition> competitions = competitionRepository.getArchiveByUser(endDate, user);
        assertNotNull(competitions);
        assertEquals(1, competitions.size());
        Competition competition = competitions.get(0);
        assertNotNull(competition);
        assertEquals("Hackathon", competition.getCompName());
    }

    @Test
    public void getRunningCompByUserTest() {
        LocalDateTime endDate = LocalDateTime.of(2020, 5, 1, 0, 0);
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
        List<Competition> competitions = competitionRepository.getRunningCompByUser(endDate, user);
        assertNotNull(competitions);
        assertEquals(1, competitions.size());
        Competition competition = competitions.get(0);
        assertNotNull(competition);
        assertEquals("Hackathon 5", competition.getCompName());
    }
}
