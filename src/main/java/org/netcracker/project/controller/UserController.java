package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleTeam;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.service.UserService;
import org.netcracker.project.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String getUser(
            @PathVariable("id") User user,
            Model model
    ){
        int winCount=0;
        int secondCount=0;
        int thirdCount=0;
        int participate=0;
        int spottedBySponsors=0;
//        for(Result result:user.getStatistics().values()){
//            switch(result){
//                case WIN:winCount++;break;
//                case SECOND:secondCount++;break;
//                case THIRD:thirdCount++;break;
//                case PARTICIPATE:participate++;break;
//                case SPOTTED:spottedBySponsors++;break;
//            }
//        }
        model.addAttribute("winCount",winCount);
        model.addAttribute("secondCount",secondCount);
        model.addAttribute("thirdCount",thirdCount);
        model.addAttribute("participate",participate);
        model.addAttribute("spotted",spottedBySponsors);
        model.addAttribute(user);
        return "user";
    }

    @PutMapping("/{id}")
    public String updateUser(
            @AuthenticationPrincipal User authUser,
            @RequestParam("password2") String password2,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ){
        if (!user.getId().equals(authUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        boolean passwordsEqual = password2.equals(user.getPassword());
        if (bindingResult.hasErrors() || !passwordsEqual) {
            Map<String, String> errors = ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            if (!passwordsEqual) model.addAttribute("passwordDiffError", "Passwords are different");
            return "redirect:/user/{id}";
        }
        userService.updateUser(authUser, user, password2);
        return "redirect:/user/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") User user,
            @RequestParam("password2") String password2,
            Model model
    ){
        if (!user.getId().equals(authUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (BCrypt.checkpw(password2, user.getPassword())) {
            model.addAttribute("passwordDiffError", "Passwords are different");
            return "redirect:/user/{id}";
        }
        userService.deleteUser(user);
        return "redirect:/login";
    }

    @GetMapping("/name/{id}")
    @ResponseBody
    public String getName(@PathVariable("id") User user) {
        return user.getSurname() + " " + user.getName() + " " + user.getSecName() + " (" + user.getUsername() + ")";
    }

    @GetMapping(value = "/team/{id}", produces = "application/json")
    @ResponseBody
    public Set<SimpleTeam> getTeams(@PathVariable("id") User user) {
        return user.getTeams().stream().map(SimpleTeam::of).collect(Collectors.toSet());
    }
}
