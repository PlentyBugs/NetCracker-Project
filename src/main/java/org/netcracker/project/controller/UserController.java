package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleTeam;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.TeamRole;
import org.netcracker.project.service.UserService;
import org.netcracker.project.util.StatisticsUtil;
import org.netcracker.project.util.ValidationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final StatisticsUtil statisticsUtil;
    private final UserService userService;


    /**
     * Принимает GET запросы на url: URL/user/{id}
     * где id - это id пользователя, чья страницы возвращается
     * @param user - Пользователь, чей id был в url
     * @param model - Объект Model, в который будут помещены какие-либо переменные, такие как переменные статистики
     * @return - Возвращает страницу профиля
     */
    @GetMapping("/{id}")
    public String getUser(
            @PathVariable("id") User user,
            Model model
    ){
        statisticsUtil.putStatisticsInModel(user, model);
        return "user";
    }

    /**
     * Принимает PUT запросы на url: URL/user/{id}
     * Используется для обновления данных пользователя
     * @param authUser - Пользователь, что совершил запрос
     * @param password2 - Пороль для подтверждения
     * @param user - Пользователь, чей id был в url, и чтя поля проверяются
     * @param bindingResult - Объект, содержащий информацию об ошибках полей объекта пользователя
     * @param model - Объект Model, в который будут помещены переменные для генерации страницы
     * @return - Возвращает перенаправление на страницу профиля пользователя
     */
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

    /**
     * Принимает PUT запросы на url: URL/user/{id}/roles
     * Используется для обновления командных ролей пользователя
     * @param authUser - Пользователь, совершивший запрос
     * @param user - Пользоватей, чей id был в url и чьи роли мы обновляем
     * @param roles - Список командных ролей для обновления
     */
    @PutMapping("/{id}/roles")
    @ResponseBody
    public void updateUserRoles(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") User user,
            @RequestBody Set<TeamRole> roles
    ) {
        if (!authUser.getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        userService.updateUserRoles(user, roles);
    }

    /**
     * Принимает PUT запросы на url: URL/user/{id}/image
     * Используется для обновления аватара пользователя
     * @param authUser - Пользователь, совершивший запрос
     * @param user - Пользователь, чей id было в url, и чей аватар будет обновлен
     * @param x - X координата начала обрезки, может быть не представлена, отсчет идет от левой грани
     * @param y - Y - координата начала обрезки, может быть не представлена, отсчет идет от верхней грани
     * @param width - Конечная ширина изображения, может быть не представлена
     * @param height - Конечная высота изображения, может быть не представлена
     * @param avatar - Объект MultipartFile хранящий изображение с аватаром, до обрезки
     * @throws IOException - Исключение, которое может быть вызвано, в случае возникновения проблем во время сохранения изображения
     */
    @PutMapping("/{id}/image")
    @ResponseBody
    public void updateAvatar(
            @AuthenticationPrincipal User authUser,
            @PathVariable("id") User user,
            @RequestParam(value = "x", required = false) Integer x,
            @RequestParam(value = "y", required = false) Integer y,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam("avatar") MultipartFile avatar
    ) throws IOException {
        if (!authUser.getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (x == null) {
            userService.saveAvatar(user, avatar);
        } else {
            userService.cropAndSaveAvatar(user, avatar, x, y, width, height);
        }
    }

    /**
     * Принимает DELETE запросы на url: URL/user/{id}
     * Используется для удаления пользователя, а точнее его деактивации
     * @param authUser - Пользователь, совершивший запрос
     * @param user - Пользователь, чей id был в url, и который будет удален
     * @param password2 - Пароль для подтверждения
     * @param model - Объект Model, в который будут помещены переменные, использующиеся для генерации страницы
     * @return - Возвращает страницу login при удачном удалении или перенаправляет на страницу профиля пользователя
     */
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

    /**
     * Принимает GET запросы на url: URL/user/name/{id}
     * Используется для получения имени пользователя в формате:
     * ИМЯ ФАМИЛИЯ (НИК)
     * @param user - Пользователь, чей id был в url, и чьи данные запрашиваются
     * @return - Строка с именем пользователя в указанном формате: ИМЯ ФАМИЛИЯ (НИК)
     */
    @GetMapping("/name/{id}")
    @ResponseBody
    public String getName(@PathVariable("id") User user) {
        return user.getSurname() + " " + user.getName() + " " + user.getSecName() + " (" + user.getUsername() + ")";
    }

    /**
     * Принимает GET запросы на url: URL/user/team/{id}
     * Используется для получения DTO SimpleTeam - команд данного пользователя в упрощенном формате без лишней или приватной информации
     * @param user - Пользователь, чей id был в url, и чьи данные запрашиваются
     * @return - Множество команд пользователя в виде DTO SimpleTeam - упрощенном формате команд с минимальным набором полей.
     */
    @GetMapping(value = "/team/{id}", produces = "application/json")
    @ResponseBody
    public Set<SimpleTeam> getTeams(@PathVariable("id") User user) {
        return user.getTeams().stream().map(SimpleTeam::of).collect(Collectors.toSet());
    }

    /**
     * Принимает GET запросы на url: URL/user/simple
     * Используется для получения списка всех пользователей в виде DTO SimpleUser
     * SimpleUser - упрощенный формат с минимальным набором полей
     * @return - Множество всех пользователей в формате SimpleUser
     */
    @GetMapping(value = "/simple", produces = "application/json")
    @ResponseBody
    public Set<SimpleUser> getAllSimpleUsers() {
        return userService.findAllSimpleUsers();
    }
}