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

    /**
     * Принимает GET запросы на url: URL/registration
     * Используется для генерации страницы с регистрацией
     * @return - Страница с регистрацией
     */
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    /**
     * Принимает POST запросы на url: URL/registration
     * Используется для регистрации пользователя
     * @param password2 - Пароль, который используется для подтверждения на случай случайной ошибки ввода пароля
     * @param roles - Множество ролей Role, с которыми пользователь регистрируется на сайте
     * @param user - Новый пользователь с проверкой полей на валидность
     * @param bindingResult - Объект BindingResult, содержащий информацию об ошибках в полях user
     * @param model - Объект Model, в который будут помещены переменные для генерации страницы.
     *             Пример: ошибки и сообщения с ошибками в случае ошибок при заполнении формы пользователя
     * @return - При удачно регистрации перенаправляет на страницу /login с сообщением об активационном коде, который был отправлен на email
     * В случае ошибки при регистрации (ошибка при вводе какого-то из полей или указанный логин уже существует)
     * возвращает на страницу регистрации с сообщениями о неверных полях
     */
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

    /**
     * Принимает GET запросы на url: URL/registration/activate/{code}
     * Метод, который используется для активации пользователя
     * @param model - Объект Model, в который будут помещены переменные для генерации страницы.
     * @param code - Код активации пользователя, который был отправлен ему на email
     * @return - Страницы /login с сообщением:
     * User successfully activated - если пользователь был успешно активирован
     * Activation code is not found - если такой код активации не был найден или произошла какая-то ошибка при активации
     */
    @GetMapping("/registration/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        String message = userService.activate(code) ? "User successfully activated" : "Activation code is not found";

        model.addAttribute("message", message);

        return "login";
    }
}
