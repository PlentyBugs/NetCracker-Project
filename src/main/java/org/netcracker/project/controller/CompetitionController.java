package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.filter.CompetitionFilterUnprepared;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.service.CompetitionService;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.ValidationUtils;
import org.netcracker.project.util.callback.DateCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService service;
    private final DateUtil dateUtil;

    @GetMapping
    public String getAllCompetitions(
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            CompetitionFilterUnprepared competitionFilterUnprepared
    ) {
        CompetitionFilter competitionFilter = competitionFilterUnprepared.prepare(dateUtil);

        Page<Competition> competitions = service.getPage(pageable, competitionFilter);
        model.addAttribute("page", competitions);
        model.addAttribute("url", "/competition");
        model.addAttribute("filter", competitionFilter);
        return "competition-list";
    }

    @PostMapping
    public String addCompetition(
            @AuthenticationPrincipal User user,
            @RequestParam("title") MultipartFile title,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @Valid Competition competition,
            BindingResult bindingResult,
            Model model
    ) throws IOException {
        if (!user.getRoles().contains(Role.ORGANIZER)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        // todo: Решить проблему по которой не получается при редиректе передать Model
        DateCallback startDateCallback = service.parseDateFromForm(startDate);
        DateCallback endDateCallback = service.parseDateFromForm(endDate);
        Map<String, String> errors = null;
        if (bindingResult.hasErrors()) {
            errors = ValidationUtils.getErrors(bindingResult);
            errors.remove("startDateError");
            errors.remove("endDateError");
            model.mergeAttributes(errors);
        }
        if (
                (errors != null && !errors.isEmpty()) ||
                startDateCallback.isFailure() ||
                endDateCallback.isFailure()
        ) {
            model.addAttribute(competition);
            if (startDateCallback.isFailure()) model.addAttribute("startDateError", "Wrong Format or Empty");
            if (endDateCallback.isFailure()) model.addAttribute("endDateError", "Wrong Format or Empty");
            return "redirect:/add-competition";
        }
        competition.setEndDate(endDateCallback.getLocalDateTime());
        competition.setStartDate(startDateCallback.getLocalDateTime());
        service.save(competition, title, user);

        return "redirect:/competition";
    }

    @GetMapping("/{id}")
    public String getCompetition(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition,
            Model model
    ) {
        model.addAttribute(competition);
        model.addAttribute("participate", user.getTeams().stream().map(e -> competition.getTeams().contains(e)).reduce(false, (x,y) -> x || y));
        return "competition";
    }

    @Async
    @DeleteMapping(value = "/{id}/team/{teamID}", produces = "application/json")
    public @ResponseBody void removeTeam(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition,
            @PathVariable("teamID") Team team
    ) {
        if (user.getId() != null && user.getId().equals(competition.getOrganizer().getId())) {
            competition.getTeams().remove(team);
            // Пока статистика не так проработана и, по сути дела, надо было бы это записывать вроде "был удален организатором"
            team.getStatistics().remove(competition);
            service.update(competition);
        }
    }

    @PostMapping("/{id}/join")
    public String join(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "team", required = false) Team team,
            @PathVariable("id") Competition competition
    ) {
        if (user.getRoles().contains(Role.PARTICIPANT) && user.getTeams().contains(team)) {
            competition.getTeams().add(team);
            service.update(competition);
        }
        return "redirect:/competition/{id}";
    }

    @PostMapping("/{id}/quit")
    public String quit(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition
    ) {
        if (user.getRoles().contains(Role.PARTICIPANT)) {
            for (Team team : user.getTeams()) {
                if (competition.getTeams().contains(team)) {
                    competition.getTeams().remove(team);
                    service.update(competition);
                    break;
                }
            }
        }
        return "redirect:/competition/{id}";
    }

    // todo: Добавить оптимизацию, чтобы выдавась соревнования за месяц, а не все (делается просто, пишу, чтобы не забыть)
    @GetMapping(value = "/calendar", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionCalendar() {
        return service.getAll();
    }

    @GetMapping(value = "/calendar/user/{id}", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionsForUser(@PathVariable("id") User user) {
        return service.getAllByUser(user);
    }

    @GetMapping(value = "/calendar/team/{id}", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionsForTeam(@PathVariable("id") Team team) {
        return service.getAllByTeam(team);
    }
}
