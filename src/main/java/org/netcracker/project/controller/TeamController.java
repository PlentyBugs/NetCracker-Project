package org.netcracker.project.controller;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.service.TeamService;
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
import org.springframework.web.util.UriUtils;

import javax.mail.Multipart;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/team")
public class TeamController {
    private final TeamService service;

    public TeamController(TeamService service) {
        this.service = service;
    }
    @GetMapping
    public String getAllTeams(Model model,
                              @RequestParam(defaultValue="",required=false) String filter,
                              @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable)
    {
        Page<Team> teams = service.getPage(pageable,filter);
        model.addAttribute("page",teams);
        model.addAttribute("add","/team");
        model.addAttribute("filter", UriUtils.encodePath(filter, "UTF-8"));
        return "team-list";
    }

    @PostMapping
    public String addTeam(@AuthenticationPrincipal User user,
                          @RequestParam ("logo") Multipart logo,
                          Model model,
                          @Valid Team team,
                          BindingResult bindingResult
                          )
        throws IOException{
            if(bindingResult.hasErrors()){
            Map<String,String> errors= ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            model.addAttribute(team);
            return "redirect:/add-team";
            }
            service.save(team,logo,user);
        return "redirect:/team";
    }
    @GetMapping("/{id}")
    public String getTeam(@PathVariable("id") Team team,
                           Model model)
    {
            model.addAttribute(team);
            return "team";
    }

    @PostMapping("/{id}/join")
    public String join(@AuthenticationPrincipal User user,
                       @PathVariable("id") Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
            team.getTeammates().add(user);
            service.update(team);
        }
        return "redirect:/team/{id}";
    }

    @PostMapping("/{id}/quit")
    public String quit(@AuthenticationPrincipal User user,
                       @PathVariable Team team
    ){
        if(user.getRoles().contains(Role.PARTICIPANT)){
                for(User u:team.getTeammates()){
                    if(team.getTeammates().contains(u)){
                        team.getTeammates().remove(u);
                        service.update(team);
                        break;
                    }
                }
        }
        return "redirect:/team/{id}";
    }

}
