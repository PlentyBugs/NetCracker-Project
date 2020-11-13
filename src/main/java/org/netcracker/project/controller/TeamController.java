package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.service.TeamService;
import org.netcracker.project.util.SecurityUtils;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;
    private final SecurityUtils securityUtils;

    @GetMapping
    public String getAllTeams(
            @AuthenticationPrincipal User user,
            Model model,
            TeamFilter teamFilter,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
    ){
        teamFilter.validate();
        Page<Team> teams = service.getPage(pageable, teamFilter, user);
        model.addAttribute("page",teams);
        model.addAttribute("add","/team");
        model.addAttribute("filter", teamFilter);
        return "team-list";
    }

    @PostMapping
    public String addTeam(@AuthenticationPrincipal User user,
                          @RequestParam ("logo") MultipartFile logo,
                          Model model,
                          @Valid Team team,
                          BindingResult bindingResult
                          )
        throws IOException{
            if(bindingResult.hasErrors()){
                Map<String,String> errors= ValidationUtils.getErrors(bindingResult);
                model.mergeAttributes(errors);
                model.addAttribute(team);
                return "/add-team";
            }
            service.save(team,logo,user);
        return "redirect:/team";
    }
    @GetMapping("/{id}")
    public String getTeam(@PathVariable("id") Team team,
                           Model model)
    {
        int winCount=0;
        int secondCount=0;
        int thirdCount=0;
        int participate=0;
        int spottedBySponsors=0;
        for(Result result:team.getStatistics().keySet() )
        {
            switch(result){
                case WIN:winCount++;break;
                case SECOND:secondCount++;break;
                case THIRD:thirdCount++;break;
                case PARTICIPATE:participate++;break;
                case SPOTTED:spottedBySponsors++;break;
            }
        }
            model.addAttribute("winCount",winCount);
            model.addAttribute("secondCount",secondCount);
            model.addAttribute("thirdCount",thirdCount);
            model.addAttribute("participate",participate);
            model.addAttribute("spotted",spottedBySponsors);

            model.addAttribute(team);
            return "team";
    }

    @PostMapping("/{id}/join")
    public String join(@AuthenticationPrincipal User user,
                       @PathVariable("id") Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
            team.getTeammates().add(user);
            user.getTeams().add(team);
            service.update(team);
            securityUtils.updateContext(user);
        }
        return "redirect:/team/{id}";
    }

    @PostMapping("/{id}/quit")
    public String quit(@AuthenticationPrincipal User user,
                       @PathVariable("id") Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
                for(User u:team.getTeammates()){
                    if(team.getTeammates().contains(u)){
                        team.getTeammates().remove(u);
                        u.getTeams().remove(team);
                        service.update(team);
                        securityUtils.updateContext(u);
                        break;
                    }
                }
        }
        return "redirect:/team/{id}";
    }

    @GetMapping("/name/{id}")
    @ResponseBody
    public String getName(@PathVariable("id") Team team) {
        return team.getTeamName();
    }
}
