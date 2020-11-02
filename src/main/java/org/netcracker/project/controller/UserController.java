package org.netcracker.project.controller;

import org.netcracker.project.model.User;
import org.netcracker.project.service.UserService;
import org.netcracker.project.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getUser(
            @PathVariable("id") User user,
            Model model
    ){
        model.addAttribute(user);
        return "user";
    }

    @PutMapping("/{id}")
    public String updateUser(
            @AuthenticationPrincipal User authUser,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ){
        if (!user.getId().equals(authUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "user";
        }
        userService.updateUser(user);
        return "redirect:/user";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") User user
    ){
        if (!user.getId().equals(authUser.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        userService.deleteUser(user);
        return "redirect:/login";
    }
}
