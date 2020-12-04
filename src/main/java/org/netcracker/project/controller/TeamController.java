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

    /**
     * Принимает GET запросы на url: URL/team/
     * Используется для получения страницы со всеми командами
     * @param user - Пользователь, который совершил запрос
     * @param model - Объект Model, в который будут переданы переменные, необходимые для генерации страницы
     * @param teamFilter - Объект TeamFilter, содержащий набор ограничений на команды, которые могут быть отображены на странице
     * @param pageable - Объект Pageable, содержащий набор настроек и использующийся для пагинации
     * @return - Страница со списком команд
     */
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

    /**
     * Принимает POST запросы на url: URL/team/
     * Используется для создания и добавления новой команды
     * @param user - Пользователь, совершивший запрос
     * @param logo - Объект MultipartFile, содержащий файл-изображения с логотипом команды
     * @param users - Список приглашенных пользователей, которые будут состоять в команде на момент ее создания, может быть не представлен
     * @param model - Объект Model, в который будут помещены переменные, необходимые для генерации страницы
     * @param team - Добавляемая команда с проверкой ее полей на валидность
     * @param bindingResult - Объект содержащий данные об ошибках в полях объекта team
     * @return - Перенаправление на страницу со списком команд в случае удачного добавления или возвращение на страницу добавления команды в ином случае
     * @throws IOException - Исключение, которое может быть выброшено при сохранении логотипа команды
     */
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

    /**
     * Принимает GET запросы на url: URL/team/{id}
     * Используется для генерации страницы профиля команды
     * @param user - Пользователь, совершивший запрос
     * @param team - Команда, чей id был в url, и чей профиль будет отображен
     * @param model - Объект Model, в который будут помещены переменные, необходмые для генерации страницы
     * @return - Страница профиля команды
     */
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

    /**
     * Принимает PUT запросы на url: URL/team/{id}/image
     * Используется для обновления логотипа команды
     * @param authUser - Пользователь, совершивший запрос
     * @param team - Команда, чей id было в url, и чье лого будет обновлено
     * @param x - X координата начала обрезки, может быть не представлена, отсчет идет от левой грани
     * @param y - Y - координата начала обрезки, может быть не представлена, отсчет идет от верхней грани
     * @param width - Конечная ширина изображения, может быть не представлена
     * @param height - Конечная высота изображения, может быть не представлена
     * @param logo - Объект MultipartFile хранящий изображение с логотипом, до обрезки
     * @throws IOException - Исключение, которое может быть вызвано, в случае возникновения проблем во время сохранения изображения
     */
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

    /**
     * Принимает POST запросы на url: URL/team/{id}/join
     * Используется для вступления в команду
     * @param user - Пользователь, совершивший запрос
     * @param team - Команда, чей id был url, и в которую вступает пользователь
     * @return - Перенаправление на страницу профиля команды
     */
    @PostMapping("/{id}/join")
    public String join(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Team team
    ) {
        if(user.getRoles().contains(Role.PARTICIPANT)){
            service.joinTeam(team, user);
        }
        return "redirect:/team/{id}";
    }

    /**
     * Принимает POST запросы на url: URL/team/{id}/quit
     * Используется для выхода из команды
     * @param user - Пользователь, совершивший запрос
     * @param team - Команда, чей id был url, и которую покидает пользователь
     * @return - Перенаправление на страницу профиля команды
     */
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

    /**
     * Принимает PUT запросы на url: URL/team/{id}/invite/{userId}
     * Используется для приглашения пользователя в команду
     * В url:
     * id - id команды, в которую приглашается пользователь
     * userId - id приглашенного пользователя
     * Запрос происходит асинхронно
     * @param team - Команда, чей id был в url, и в которую приглашают пользователя
     * @param user - Пользователь, чей id (userId) был в url, и который вступает в команду
     */
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

    /**
     * Принимает PUT запросы на url: URL/team/{id}/role/{userId}
     * Используется для сохранения списка командных ролей пользователя для этой команды
     * В url:
     * id - id команды, для которой пользователь выбрал определенный список командных ролей
     * userId - id пользователя, который изменяет список командных ролей, с которыми он выступает от этой команды
     * Запрос происходит асинхронно
     * @param authUser - Пользователь, совершивший запрос
     * @param team - Команда, чей id был в url, и для которой пользователь изменяет список своих командных ролей
     * @param user - Пользователь, чей id (userId) был в url, и который изменяет список своих командных ролей
     * @param teamRoles - Множество командных ролей TeamRole
     */
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

    /**
     * Принимает GET запросы на url: URL/team/name/{id}/
     * Используется для получения названия команды
     * @param team - Команда, чей id был в url, и чье название запрашивается
     * @return - Строка, содержащая название запрашиваемой команды
     */
    @GetMapping("/name/{id}")
    @ResponseBody
    public String getName(@PathVariable("id") Team team) {
        return team.getTeamName();
    }
}
