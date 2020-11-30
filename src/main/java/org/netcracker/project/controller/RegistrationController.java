package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.service.UserService;
import org.netcracker.project.util.ValidationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam("password2") String password2,
            @RequestParam(value = "role", required = false) Set<Role> roles,
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ) {
        boolean error = false;

        if (!Objects.equals(user.getPassword(), password2)) {
            model.addAttribute("passwordDiffError", "Passwords are different");
            error = true;
        }

        if (password2 == null || password2.equals("")) {
            model.addAttribute("password2", "Password confirmation can't be empty");
            error = true;
        }

        if (bindingResult.hasErrors() || error) {
            Map<String, String> errorsMap = ValidationUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", user);

            return "registration";
        }

        if (roles != null ? !userService.create(user, roles) : !userService.create(user)) {
            model.addAttribute("usernameError", "User exists");
            model.addAttribute("user", user);
            return "registration";
        }

        return "redirect:/login?activate=true";
    }

    @GetMapping("/registration/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        String message = userService.activate(code) ? "User successfully activated" : "Activation code is not found";

        model.addAttribute("message", message);

        return "login";
    }
}
