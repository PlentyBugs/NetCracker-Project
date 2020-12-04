package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.embeddable.UserTeamRole;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.model.enums.TeamRole;
import org.netcracker.project.service.TeamService;
import org.netcracker.project.util.StatisticsUtil;
import org.netcracker.project.util.ValidationUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final StatisticsUtil statisticsUtil;
    private final TeamService service;

    @GetMapping
    public String getAllTeams(
            @AuthenticationPrincipal User user,
            Model model,
            TeamFilter teamFilter,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ){
        teamFilter.validate();
        Page<Team> teams = service.getPage(pageable, teamFilter, user);
        model.addAttribute("page", teams);
        model.addAttribute("url","/team");
        model.addAttribute("filter", teamFilter);
        return "team-list";
    }

    @PostMapping
    public String addTeam(
            @AuthenticationPrincipal User user,
            @RequestParam ("logo") MultipartFile logo,
            @RequestParam(value = "invited-user", required = false) Set<User> users,
            Model model,
            @Valid Team team,
            BindingResult bindingResult
    ) throws IOException{
        if(bindingResult.hasErrors()){
            Map<String,String> errors = ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute(team);
            return "/add-team";
        }
        service.save(team, logo, user, users);
        return "redirect:/team";
    }

    @GetMapping("/{id}")
    public String getTeam(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Team team,
            Model model
    ) {
        Long userId = user.getId();
        model.addAttribute("in", team.getTeammates().stream().map(User::getId).anyMatch(e -> e.equals(userId)));

        Set<UserTeamRole> userTeamRoles = team.getUserTeamRoles();
        Map<Long, Set<TeamRole>> userIdTeamRole = new HashMap<>();
        for (UserTeamRole utr : userTeamRoles) {
            Long id = utr.getUserId();
            Set<TeamRole> teamRoleSet = userIdTeamRole.getOrDefault(id, new HashSet<>());
            teamRoleSet.add(utr.getTeamRole());
            userIdTeamRole.put(id, teamRoleSet);
        }
        model.addAttribute("userIdTeamRole", userIdTeamRole);
        model.addAttribute("userHasTeamRoles", userIdTeamRole.containsKey(userId));
        model.addAttribute("userTeamRolesInTeam", userIdTeamRole.get(userId));

        statisticsUtil.putStatisticsInModel(team, model);
        return "team";
    }

    @PutMapping("/{id}/image")
    @ResponseBody
    public void updateAvatar(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") Team team,
            @RequestParam(value = "x", required = false) Integer x,
            @RequestParam(value = "y", required = false) Integer y,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam("avatar") MultipartFile logo
    ) throws IOException {
        if (team.getTeammates().stream().map(User::getId).noneMatch(e -> e.equals(authUser.getId()))) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (x == null) {
            service.saveLogo(team, logo);
        } else {
            service.cropAndSaveLogo(team, logo, x, y, width, height);
        }
    }

    @PostMapping("/{id}/join")
    public String join(@AuthenticationPrincipal User user,
                       @PathVariable("id") Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
            service.joinTeam(team, user);
        }
        return "redirect:/team/{id}";
    }

    @PostMapping("/{id}/quit")
    public String quit(@AuthenticationPrincipal User user,
                       @PathVariable("id") Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
            Long userId = user.getId();
            Set<User> teammates = team.getTeammates();
            if (teammates.stream().map(User::getId).anyMatch(e -> e.equals(userId))) {
                for(User u : teammates){
                    if(u.getId().equals(userId)){
                        service.quitTeam(team, u);
                        break;
                    }
                }
            }
        }
        return "redirect:/team/{id}";
    }

    @Async
    @PutMapping(value = "/{id}/invite/{userId}")
    @ResponseBody
    public void inviteUser(
            @PathVariable("id") Team team,
            @PathVariable("userId") User user
    ) {
        // Тут возможно расширение и изменение, если вдруг будет добавлена система именно приглашений, а не просто добавления в команду
        service.joinTeam(team, user);
    }

    @Async
    @PutMapping(value = "/{id}/role/{userId}", consumes = "application/json")
    @ResponseBody
    public void saveTeamRoles(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") Team team,
            @PathVariable("userId") User user,
            @RequestBody Set<TeamRole> teamRoles
    ) {
        if (!user.getId().equals(authUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        service.saveTeamRolesByUser(team, user, teamRoles);
    }

    @GetMapping("/name/{id}")
    @ResponseBody
    public String getName(@PathVariable("id") Team team) {
        return team.getTeamName();
    }
}
