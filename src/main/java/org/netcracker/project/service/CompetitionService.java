package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.repository.CompetitionRepository;
import org.netcracker.project.repository.RegisteredTeamRepository;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.callback.DateCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository repository;
    private final RegisteredTeamRepository registeredTeamRepository;
    private final ImageUtils imageUtils;
    private final DateUtil dateUtil;

    public Page<Competition> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Competition> getPage(Pageable pageable, CompetitionFilter filter) {
        if (filter.isEqualsBoundsOn()) {
            if (filter.isEnableEqualsStart() && filter.isEnableEqualsEnd()) {
                return repository.findAllByStartDateEqualsAndEndDateEquals(pageable, filter.getEqualsStart(), filter.getEqualsEnd(), filter.getString());
            } else if (filter.isEnableEqualsStart()) {
                return repository.findAllByStartDateEquals(pageable, filter.getEqualsStart(), filter.getString());
            } else {
                return repository.findAllByEndDateEquals(pageable, filter.getEqualsEnd(), filter.getString());
            }
        }

        if (filter.isBoundsOn()) {
            if (!filter.isEnableBeforeStart() || filter.getBeforeStart() == null) filter.setBeforeStart(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableBeforeEnd() || filter.getBeforeEnd() == null) filter.setBeforeEnd(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableAfterStart() || filter.getAfterStart() == null) filter.setAfterStart(LocalDateTime.now().minusYears(100));
            if (!filter.isEnableAfterEnd() || filter.getAfterEnd() == null) filter.setAfterEnd(LocalDateTime.now().minusYears(100));
            return repository.findAllByBounds(pageable, filter.getBeforeStart(), filter.getAfterStart(), filter.getBeforeEnd(), filter.getAfterEnd(), filter.getFormattedString());
        }

        return repository.findAllBySearch(pageable, filter.getFormattedString());
    }

    public boolean save(Competition competition, MultipartFile title, User user) throws IOException {
        competition.setOrganizer(user);
        competition.setCompEnded(false);  //флаг окончания сначала false, после соревнования - true. Можно использовать, чтобы дизейблить.
        saveTitle(competition, title);
        repository.save(competition);
        return true;
    }

    public boolean update(Competition competition) {
        repository.save(competition);
        return true;
    }


    private void saveTitle(@Valid Competition competition, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = imageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            competition.setTitleFilename(resultFilename);
        }
    }

    public DateCallback parseDateFromForm(String formDate) {
        return dateUtil.parseDateFromForm(formDate);
    }

    public List<Competition> getAllByUserCalendar(User user, String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllByUserCalendar(user, startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    public List<Competition> getAllByTeamCalendar(Team team, String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllByTeamCalendar(team.getId(), startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    public List<Competition> getAllCalendar(String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllCalendar(startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    public void addTeam(Competition competition, Team team) {
        team.getTeammates().remove(competition.getOrganizer());
        RegisteredTeam registeredTeam = RegisteredTeam.of(team);
        System.out.println(competition);
        System.out.println(team);
        System.out.println(registeredTeam);
        registeredTeamRepository.save(registeredTeam);
        competition.getTeams().add(registeredTeam);
        update(competition);
    }

    public void removeTeamByUser(Competition competition, User user) {
        Set<Long> teams = competition.getTeams().stream().map(RegisteredTeam::getId).collect(Collectors.toSet());
        for (Team team : user.getTeams()) {
            if (teams.contains(team.getId())) {
                competition.getTeams().remove(RegisteredTeam.of(team));
                update(competition);
                break;
            }
        }
    }

    public Page <Competition> getAllEndedCompetitions(Pageable pageable,User user){   //прошедшие соревнования
                 LocalDateTime today = LocalDateTime.now();
                    return repository.getArchiveByUser(pageable,today,user);
    }

    public Page <Competition> getAllActingCompetitions(Pageable pageable, User user){
        LocalDateTime today=LocalDateTime.now();
        return repository.getRunningCompByUser(pageable,today,user);
    }

    public void gradeCompetition(Competition competition, RegisteredTeam winner, RegisteredTeam second, RegisteredTeam third, Set<RegisteredTeam> spotted) {
        Set<RegisteredTeam> teams = competition.getTeams();
        for (RegisteredTeam team : teams) {
            Result result = Result.PARTICIPATE;
            if (team.equals(winner)) {
                result = Result.WIN;
            } else if (team.equals(second)) {
                result = Result.SECOND;
            } else if (team.equals(third)) {
                result = Result.THIRD;
            } else if (spotted.contains(team)) {
                result = Result.SPOTTED;
            }
            gradeOneTeam(team, result, competition);
        }
    }

    private void gradeOneTeam(RegisteredTeam team, Result result, Competition competition) {
        // Установить для команды
        for (User u : team.getTeammates()) {
            // Установить для каждого пользователя
        }
    }
}
