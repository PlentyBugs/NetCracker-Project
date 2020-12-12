package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.repository.CompetitionRepository;
import org.netcracker.project.repository.RegisteredTeamRepository;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.callback.DateCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final RegisteredTeamRepository registeredTeamRepository;
    private final CompetitionRepository repository;
    private final TeamService teamService;
    private final UserService userService;
    private final RoomService roomService;
    private final ImageUtils imageUtils;
    private final DateUtil dateUtil;

    /**
     * Метод, который возвращает страницу соревнований по заданным настройкам страницы
     * @param pageable Объект Pageable с настройками страницы
     * @return Страница с соревнованиями с заданными настройками
     */
    public Page<Competition> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Метод, который возвращает страницу соревнований по заданным настройкам страницы и удовлетворяющий всем фильтрам
     * @param pageable Объект Pageable с настройками страницы
     * @param filter Фильтры для соревнований
     * @return Страницы с соревнованиями с заданными настройками и удовлетворяющий всем фильтрам
     */
    public Page<Competition> getPage(Pageable pageable, CompetitionFilter filter) {
        if (filter.isEqualsBoundsOn()) {
            if (filter.isEnableEqualsStart() && filter.isEnableEqualsEnd()) {
                return repository.findAllByStartDateEqualsAndEndDateEquals(pageable, filter.getEqualsStart(), filter.getEqualsEnd(), filter.getString());
            } else if (filter.isEnableEqualsStart()) {
                return repository.findAllByStartDateEquals(pageable, filter.getEqualsStart(), filter.getString());
            } else {
                return repository.findAllByEndDateEquals(pageable, filter.getEqualsEnd(), filter.getString());
            }
        }

        if (filter.isBoundsOn()) {
            if (!filter.isEnableBeforeStart() || filter.getBeforeStart() == null) filter.setBeforeStart(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableBeforeEnd() || filter.getBeforeEnd() == null) filter.setBeforeEnd(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableAfterStart() || filter.getAfterStart() == null) filter.setAfterStart(LocalDateTime.now().minusYears(100));
            if (!filter.isEnableAfterEnd() || filter.getAfterEnd() == null) filter.setAfterEnd(LocalDateTime.now().minusYears(100));
            return repository.findAllByBounds(pageable, filter.getBeforeStart(), filter.getAfterStart(), filter.getBeforeEnd(), filter.getAfterEnd(), filter.getFormattedString());
        }

        return repository.findAllBySearch(pageable, filter.getFormattedString());
    }

    /**
     * Метод, который сохраняет соревнование
     * @param competition Сохраняемое соревнование
     * @param title Лого соревнования
     * @param user Пользователь, который будет считаться организатором соревнования
     * @return Булево значение, true - если соревнование создалось без ошибок
     * @throws IOException - Исключение, которое может быть выброшено, если возникнет ошибка сохранения лого соревнования
     */
    public boolean save(Competition competition, MultipartFile title, User user) throws IOException {
        competition.setOrganizer(user);
        competition.setCompEnded(false);  //флаг окончания сначала false, после соревнования - true. Можно использовать, чтобы дизейблить.
        saveTitle(competition, title);
        createGroupChat(competition);
        repository.save(competition);
        return true;
    }

    /**
     * Метод, который обновляет соревнование
     * @param competition Обновляемое соревнование
     * @return Булево значение, true - если обновление прошло без ошибок
     */
    public boolean update(Competition competition) {
        repository.save(competition);
        return true;
    }

    /**
     * Метод, который сохраняет лого соревнования
     * @param competition Соревнование, чье лого сохраняется
     * @param file Файл с изображением логотипа
     * @throws IOException - Исключение, которое может быть выброшено, если возникнет ошибка сохранения лого соревнования
     */
    private void saveTitle(@Valid Competition competition, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = imageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            competition.setTitleFilename(resultFilename);
        }
    }

    /**
     * Метод, который вызывает метод DateUtil parseDateFromForm
     * Он парсит дату, которая была получена от клиента из формы
     * @param formDate Строка, содержащая дату, полученную от клиента из формы
     * @return Коллбек, в котором хранится 2 значения: объект даты и булево значение,
     *                если булево значение true, то парсинг прошел без проблем и можно брать объект,
     *                иначе произошла какая-то ошибка и объект равен null
     */
    public DateCallback parseDateFromForm(String formDate) {
        return dateUtil.parseDateFromForm(formDate);
    }

    /**
     * Метод, который возвращает все соревнования по пользователю для календаря
     * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
     * @param user Пользователь, чьи соревнования будут показаны
     * @param startDate Дата начала месяца из календаря
     * @return Список соревнований по пользователю за определенный месяц
     */
    public List<Competition> getAllByUserCalendar(User user, String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllByUserCalendar(user, startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    /**
     * Метод, который возвращает все соревнования по команде для календаря
     * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
     * @param team Команда, чьи соревнования будут показаны
     * @param startDate Дата начала месяца из календаря
     * @return Список соревнований по команде за определенный месяц
     */
    public List<Competition> getAllByTeamCalendar(Team team, String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllByTeamCalendar(team.getId(), startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    /**
     * Метод, который возвращает все соревнования для календаря
     * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
     * @param startDate Дата начала месяца из календаря
     * @return Список соревнований за определенный месяц
     */
    public List<Competition> getAllCalendar(String startDate) {
        LocalDateTime startOfMonthDate = LocalDateTime.parse(startDate);
        return repository.findAllCalendar(startOfMonthDate, startOfMonthDate.plusDays(35));
    }

    /**
     * Метод, который регистрирует команду на участие в соревновании
     * @param competition Соревнование, в которое происходит запись
     * @param team Команда, которая записывается на соревнование
     */
    public void addTeam(Competition competition, Team team) {
        RegisteredTeam registeredTeam = RegisteredTeam.of(team);

        Set<User> teammates = registeredTeam.getTeammates();
        teammates.remove(competition.getOrganizer());
        Set<Long> participants = competition.getTeams().stream().flatMap(e -> e.getTeammates().stream()).map(User::getId).collect(Collectors.toSet());
        teammates.removeIf(user -> participants.contains(user.getId()));

        roomService.addGroupMembers(competition.getGroupChatId(), teammates.stream().map(User::getId).map(Object::toString).collect(Collectors.toSet()));
        registeredTeamRepository.save(registeredTeam);
        competition.getTeams().add(registeredTeam);
        update(competition);
    }

    /**
     * Метод, который отписывает команду от участия в соревновании
     * @param competition Соревнование, от которого отписывается команда
     * @param user Команда, которая отписывается от соревнования
     */
    public void removeTeamByUser(Competition competition, User user) {
        Set<Long> teams = competition.getTeams().stream().map(RegisteredTeam::getId).collect(Collectors.toSet());
        for (Team team : user.getTeams()) {
            if (teams.contains(team.getId())) {
                RegisteredTeam registeredTeam = RegisteredTeam.of(team);
                competition.getTeams().remove(registeredTeam);
                roomService.removeGroupMembers(competition.getGroupChatId(), registeredTeam.getTeammates().stream().map(User::getId).map(Object::toString).collect(Collectors.toSet()));
                update(competition);
                break;
            }
        }
    }

    /**
     * Метод, который возвращает все законченные соревнования, где указанный пользователь является организатором
     * @param user Пользователь, который является организатором соревнований
     * @return Список законченных соревнований, для который указанный пользователь - организатор
     */
    public List<Competition> getAllEndedCompetitions(User user){   //прошедшие соревнования
                 LocalDateTime today = LocalDateTime.now();
                    return repository.getArchiveByUser(today,user);
    }

    /**
     * Метод, который возвращает все действующие соревнования, где указанный пользователь является организатором
     * @param user Пользователь, который является организатором соревнований
     * @return Список действующих соревнований, для который указанный пользователь - организатор
     */
    public List<Competition> getAllActingCompetitions(User user){
        LocalDateTime today=LocalDateTime.now();
        return repository.getRunningCompByUser(today,user);
    }

    /**
     * Метод, в котором идет оценивание всех команд, участвующих в соревновании
     * @param competition Соревнование, команды которой оцениваются
     * @param winner Победитель соревнования
     * @param second Команда, занявшая второе место
     * @param third Команда, занявшая третье место
     * @param spotted Множество команд, которые были замечены спонсорами
     */
    public void gradeCompetition(Competition competition, RegisteredTeam winner, RegisteredTeam second, RegisteredTeam third, Set<RegisteredTeam> spotted) {
        Set<RegisteredTeam> teams = competition.getTeams();
        for (RegisteredTeam team : teams) {
            gradeOneTeam(team, Result.PARTICIPATE, competition);
            if (team.equals(winner)) {
                gradeOneTeam(team, Result.WIN, competition);
            }
            if (team.equals(second)) {
                gradeOneTeam(team, Result.SECOND, competition);
            }
            if (team.equals(third)) {
                gradeOneTeam(team, Result.THIRD, competition);
            }
            if (spotted != null && spotted.contains(team)) {
                gradeOneTeam(team, Result.SPOTTED, competition);
            }
        }
    }

    /**
     * Метод, в котором происходит оценка участия команды и ее участников
     * В нем находится команда Team с по RegisteredTeam Id и уже она оценивается
     * Но оценка участников происходит именно для состава RegisteredTeam
     * @param team Команда из Competition
     * @param result Результат участия, который будет присужден аналогичной Team и участникам RegisteredTeam
     * @param competition Соревнование, в котором принимала участие команда
     */
    private void gradeOneTeam(RegisteredTeam team, Result result, Competition competition) {
        teamService.gradeTeam(team, result, competition);
        for (User user : team.getTeammates()) {
            userService.gradeUser(user, result, competition);
        }
    }

    /**
     * Метод, который создает групповой чат для соревнования
     * @param competition Соревнование, для которого будет создан групповой чат
     */
    public void createGroupChat(Competition competition) {
        String groupChatId = UUID.randomUUID().toString();
        competition.setGroupChatId(groupChatId);
        String adminId = competition.getOrganizer().getId().toString();
        roomService.createGroupRoomWithGivenChatId(adminId, Set.of(adminId), competition.getCompName(), groupChatId);
    }
}
