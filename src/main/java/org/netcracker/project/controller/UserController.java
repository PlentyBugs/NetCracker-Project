package org.netcracker.project.controller;

import org.netcracker.project.model.User;
import org.netcracker.project.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUser(@RequestParam Long id){
        return userService.getUser(id);
    }

    @PutMapping
    public String updateUser(@AuthenticationPrincipal User userForUpdate){
        return userService.updateUser(userForUpdate);
    }

    @DeleteMapping
    public String deleteUser(@RequestParam Long id){
        return userService.deleteUser(id);
    }
}
