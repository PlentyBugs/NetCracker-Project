package org.netcracker.project.controller;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.service.CompetitionService;
import org.netcracker.project.util.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/competition")
public class CompetitionController {

    private final CompetitionService service;

    public CompetitionController(CompetitionService service) {
        this.service = service;
    }

    @GetMapping
    public String getAllCompetitions(
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "", required = false) String filter
    ) {
        Page<Competition> competitions = service.getPage(pageable, filter);
        model.addAttribute("page", competitions);
        model.addAttribute("url", "/competition");
        model.addAttribute("filter", UriUtils.encodePath(filter, "UTF-8"));
        return "competition-list";
    }

    @PostMapping
    public String addCompetition(
            @AuthenticationPrincipal User user,
            @Valid Competition competition,
            BindingResult bindingResult,
            Model model,
            @RequestParam("title") MultipartFile title
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
        } else {
            service.save(competition, title, user);
        }

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

    @PostMapping("/{id}/signup")
    public String signup(
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

    @PostMapping("/{id}/logout")
    public String logout(
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
}
