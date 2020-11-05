package org.netcracker.project.service;


import org.junit.jupiter.api.Test;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.CompetitionRepository;
import org.netcracker.project.util.DateUtil;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
public class CompetitionServiceTest {

    @MockBean
    private CompetitionRepository competitionRepository;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private DateUtil dateUtil;

    @Test
    public void getPageTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Competition competition = new Competition();
        Page<Competition> competitionPage = new PageImpl<>(Collections.singletonList(competition));

        when(competitionService.getPage(pageable)).thenReturn(competitionPage);

        Page<Competition> competitions = competitionService.getPage(pageable);

        assertNotNull(competitions);
        assertEquals(competitions.getNumberOfElements(), 1);
        verify(competitionRepository, times(1)).findAll(pageable);
    }

    @Test
    public void saveTest() throws IOException {
        MultipartFile file = new MockMultipartFile("abc.png", "123".getBytes());
        Competition competition = new Competition();
        User user = new User();

        assertTrue(competitionService.save(competition, file, user));
        verify(competitionRepository, times(1)).save(competition);
    }

    @Test
    public void updateTest() {
        Competition competition = new Competition();

        assertTrue(competitionService.update(competition));
        verify(competitionRepository, times(1)).save(competition);
    }

    @Test
    public void getPageFilterAfterTest() {
        getPageFilterTest("after");
    }

    @Test
    public void getPageFilterBeforeTest() {
        getPageFilterTest("before");
    }

    @Test
    public void getPageFilterEqualsTest() {
        getPageFilterTest("equals");
    }

    private void getPageFilterTest(String command) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Competition> competitionPage = new PageImpl<>(List.of());
        String date = "2021-10-10T20:20";
        String filter = command + date;

        when(competitionService.getPage(pageable, filter)).thenReturn(competitionPage);

        Page<Competition> competitions = competitionService.getPage(pageable, filter);

        assertNotNull(competitions);
        assertEquals(competitions.getNumberOfElements(), 0);
        switch (command) {
            case "before": verify(competitionRepository, times(1)).findAllByStartDateBefore(pageable, dateUtil.compileFilter(date, command)); break;
            case "after": verify(competitionRepository, times(1)).findAllByStartDateAfter(pageable, dateUtil.compileFilter(date, command)); break;
            case "equals": verify(competitionRepository, times(1)).findAllByStartDateEquals(pageable, dateUtil.compileFilter(date, command)); break;
            default: break;
        }
    }
}
