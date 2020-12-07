package org.netcracker.project.controller;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.filter.CompetitionFilterUnprepared;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.model.enums.Theme;
import org.netcracker.project.service.CompetitionService;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.ValidationUtils;
import org.netcracker.project.util.callback.DateCallback;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService service;
    private final DateUtil dateUtil;

    /**
     * Принимает GET запросы на url:URL/competition
     * Используется для вывода списка соревнований
     * @param model - объект Model, в котором будут переменные для вывода соревнований.
     * @param pageable - объект Pageable для пагинации
     * @param competitionFilterUnprepared - ?
     * @return - Страница со списком соревнований
     */
    @GetMapping
    public String getAllCompetitions(
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            CompetitionFilterUnprepared competitionFilterUnprepared
    ) {
        CompetitionFilter competitionFilter = competitionFilterUnprepared.prepare(dateUtil);

        Page<Competition> competitions = service.getPage(pageable, competitionFilter);
        model.addAttribute("page", competitions);
        model.addAttribute("url", "/competition");
        model.addAttribute("filter", competitionFilter);
        return "competition-list";
    }

    /**
     * Принимает POST запросы на url: URL/competition
     * @param user - пользователь, который сделал запрос
     * @param title - объект MultipartFile, является логотипом соревнования
     * @param startDate - дата начала соревнования
     * @param endDate - дата окончания соревнования
     * @param themes - метки, которые определяют вид соревнования
     * @param competition - добавляемое соревнование с валидацией
     * @param bindingResult - объект, который содержит данные об ошибках в competition
     * @param model - объект Model,в который будут записаны переменные
     * @return - в случае успеха перенаправляет по url: URL/competition
     * @throws IOException - исключение, которое появляется, если не указаны начальная дата и конечная
     */
    @PostMapping
    public String addCompetition(
            @AuthenticationPrincipal User user,
            @RequestParam("title") MultipartFile title,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "theme", required = false) Set<Theme> themes,
            @Valid Competition competition,
            BindingResult bindingResult,
            Model model
    ) throws IOException {
        if (themes != null) competition.setThemes(themes);
        if (!user.getRoles().contains(Role.ORGANIZER)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        DateCallback startDateCallback = service.parseDateFromForm(startDate);
        DateCallback endDateCallback = service.parseDateFromForm(endDate);
        Map<String, String> errors = null;
        if (bindingResult.hasErrors()) {
            errors = ValidationUtils.getErrors(bindingResult);
            errors.remove("startDateError");
            errors.remove("endDateError");
            model.mergeAttributes(errors);
        }
        if (
                (errors != null && !errors.isEmpty()) ||
                startDateCallback.isFailure() ||
                endDateCallback.isFailure()
        ) {
            model.addAttribute(competition);
            if (startDateCallback.isFailure()) model.addAttribute("startDateError", "Wrong Format or Empty");
            if (endDateCallback.isFailure()) model.addAttribute("endDateError", "Wrong Format or Empty");
            return "/add-competition";
        }
        competition.setEndDate(endDateCallback.getLocalDateTime());
        competition.setStartDate(startDateCallback.getLocalDateTime());
        competition.setGroupChatId(UUID.randomUUID().toString());
        service.save(competition, title, user);

        return "redirect:/competition";
    }

    /**
     * Принимает GET запросы на url: URL/competition/{id}
     * Позволяет получить
     * @param user - пользователь, сделавший запрос
     * @param competition
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    public String getCompetition(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition,
            Model model
    ) {
        model.addAttribute(competition);
        model.addAttribute("participate", user.getTeams().stream().map(RegisteredTeam::of).map(e -> competition.getTeams().contains(e)).reduce(false, (x, y) -> x || y));
        model.addAttribute("expired", competition.getEndDate().compareTo(LocalDateTime.now()) < 0);
        return "competition";
    }

    /**
     * Принимает PUT запросы на url: URL/competition/{id}/grade
     * Используется для оценивания команд по окончании соревнования
     * @param user - Пользователь, сделавший запрос
     * @param competition - Соревнование,
     * @param winner - команда, которая победила
     * @param second - команда, занявшая второе место
     * @param third - команда, занявшая третье место
     * @param spotted - команда(ы), замеченная(ые) спонсорами
     */
    @Async
    @PutMapping(value = "/{id}/grade", produces = "application/json")
    public @ResponseBody void grade(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition,
            @RequestParam("winner") RegisteredTeam winner,
            @RequestParam(value = "second", required = false) RegisteredTeam second,
            @RequestParam(value = "third", required = false) RegisteredTeam third,
            @RequestParam(value = "spotted", required = false) Set<RegisteredTeam> spotted
    ) {
        service.gradeCompetition(competition, winner, second, third, spotted);
    }

    /**
     * Принимает DELETE запросы на url: URL/competition/{id}/team/{teamID}
     * Удаляет команду с соревнования
     * Запрос происходит асинхронно
     * @param user - Пользователь, сделавший запрос
     * @param competition - Соревнование, id которого в url, из него удаляется команда
     * @param team - Команда, id которой в url, удаляется из соревнования
     */
    @Async
    @DeleteMapping(value = "/{id}/team/{teamID}", produces = "application/json")
    public @ResponseBody void removeTeam(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition,
            @PathVariable("teamID") Team team
    ) {
        if (user.getId() != null && user.getId().equals(competition.getOrganizer().getId())) {
            competition.getTeams().remove(RegisteredTeam.of(team));
            // Пока статистика не так проработана и, по сути дела, надо было бы это записывать вроде "был удален организатором"
            team.getStatistics().remove(competition);
            service.update(competition);
        }
    }

    /**
     * Принимает POST запросы на url: URL/competition/{id}/join
     * Присоединяет пользователя к соревнованию.
     * @param user - Пользователь, сделавший запрос
     * @param team - Команда, от которой пользователь присоединяется
     * @param competition - Соревенование, по id которого из url присоединяется user
     * @return - Перенаправление на страницу соревнования
     */
    @PostMapping("/{id}/join")
    public String join(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "team", required = false) Team team,
            @PathVariable("id") Competition competition
    ) {
        if (user.getRoles().contains(Role.PARTICIPANT) && user.getTeams().contains(team)) {
            service.addTeam(competition, team);
        }
        return "redirect:/competition/{id}";
    }

    /**
     * Принимает POST запросы на url: URL/competition/{id}/quit
     * Позволяет пользователю выйти из соревнования
     * @param user - Пользователь, сделавший запрос
     * @param competition - Соревнование, id которого в url, его покидает пользователь
     * @return - Перенаправление на страницу соревнования
     */
    @PostMapping("/{id}/quit")
    public String quit(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Competition competition
    ) {
        if (user.getRoles().contains(Role.PARTICIPANT)) {
            service.removeTeamByUser(competition, user);
        }
        return "redirect:/competition/{id}";
    }

    /**
     * Принимает GET запросы на url: URL/competition/calendar
     * Ищет все соревнования по дате начала
     * @param startDate - Дата начала соревнования
     * @return - Список соревнований
     */
    @GetMapping(value = "/calendar", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionCalendar(@RequestParam String startDate) {
        return service.getAllCalendar(startDate);
    }

    /**
     * Принимает GET запросы по url: URl/competition/calendar/user/{id}
     * Получение списка соревнований для конкретного пользователя
     * @param user - Пользователь, сделавший запрос
     * @param startDate - Дата начала соревнования
     * @return - Список всех соревнований user, начинающихся со startDate
     */
    @GetMapping(value = "/calendar/user/{id}", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionsForUser(
            @PathVariable("id") User user,
            @RequestParam String startDate
    ) {
        return service.getAllByUserCalendar(user, startDate);
    }

    /**
     * Принимает GET запросы по url: URL/competition/calendar/team/{id}
     * Получение списка соревнований для конкретной команды
     * @param team - Команда, для которой выводится список соревнований
     * @param startDate - Дата начала соревнования
     * @return - Список всех соревнований team, начинающихся со startDate
     */
    @GetMapping(value = "/calendar/team/{id}", produces = "application/json")
    @ResponseBody
    public List<Competition> getAllCompetitionsForTeam(
            @PathVariable("id") Team team,
            @RequestParam String startDate
    ) {
        return service.getAllByTeamCalendar(team, startDate);
    }

    /**
     * Принимает GET запросы на url: URL/competition/mycomp
     * Выводит страничку с прошедшими и текущими соревнованиями для пользователя
     * @param user - пользователь, сделавший запрос
     * @return - Страница с соревнованиями
     */
    @GetMapping("/mycomp")
    String getmyComp(@AuthenticationPrincipal User user){ return "mycomp"; }

    /**
     * Принимает GET запросы на url: URL/competition/mycomp/archive
     * Используется для получения списка прошедших соревнований организатора
     * @param user - пользователь, сделавший запрос
     * @return - Список соревнований, которые закончились
     */
    @GetMapping(value = "/mycomp/archive", produces = "application/json")
    @ResponseBody
    public List<Competition> getArchive(
            @AuthenticationPrincipal User user
    ) {
            return service.getAllEndedCompetitions(user);
    }

    /**
     * Принимает GET запросы на url: URL/competition/mycomp/running
     * Используется для получения списка текущих соревнований организатора
     * @param user - пользователь, сделавший запрос
     * @return - Список соревнований, которые на данный момент проходят
     */
    @GetMapping(value = "/mycomp/running", produces = "application/json")
    @ResponseBody
    public List<Competition> getRunningComp(
            @AuthenticationPrincipal User user
    ) {
        return service.getAllActingCompetitions(user);
    }
}
