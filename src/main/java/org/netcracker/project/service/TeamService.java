package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.embeddable.Statistics;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final SecurityUtils securityUtils;
    private final TeamRepository repository;
    private final RoomService roomService;
    private final ImageUtils imageUtils;

    /**
     * Метод, который возвращает страницу со всеми командами
     * @param pageable - Объект Pageable, полученный из контроллера
     * @return - Объект Page, содержащий страницу с командами с указанными настройками
     */
    public Page<Team> getPage(Pageable pageable){return repository.findAll(pageable);}

    /**
     * Метод, который возвращает страницу со всеми удовлетворяющими фильтрам командами
     * @param pageable - Объект Pageable, полученный из контроллера
     * @param filter - Объект TeamFilter, содержащий фильтры для команд
     * @param user - пользователь, совершивший запрос и нужный для фильтров
     * @return - Объект Page, содержащий страницу с командами с указанными настройками страницы и удовлетворяющий всем фильтрам
     */
    public Page<Team> getPage(Pageable pageable, TeamFilter filter, User user) {
        if (filter.getRemoveEmpty()) {
            if (filter.getMinMembers() <= 0)
                filter.setMinMembers(1);
            if (filter.getMaxMembers() <= 0)
                filter.setMaxMembers(1);
        }
        if (filter.getAlreadyInTheGroup()) {
            return repository.findAllWithFilterAndWithoutMe(
                    pageable,
                    filter.getMinMembersOn() ? filter.getMinMembers() : filter.getRemoveEmpty() ? 1 : -1,
                    filter.getMaxMembersOn() ? filter.getMaxMembers() : Integer.MAX_VALUE,
                    filter.getFormattedSearchName(),
                    user
            );
        }
        return repository.findAllWithFilter(
                pageable,
                filter.getMinMembersOn() ? filter.getMinMembers() : filter.getRemoveEmpty() ? 1 : -1,
                filter.getMaxMembersOn() ? filter.getMaxMembers() : Integer.MAX_VALUE,
                filter.getFormattedSearchName()
        );
    }

    /**
     * Метод, который сохраняет команду
     * @param team - сохраняемая команда
     * @param logo - Файл с логотипом команды
     * @param user - Пользователь, который создал команду
     * @return - Булево значение, true - если команда удачно сохранена
     * @throws IOException - Исключение, которое может быть выброшено в случае ошибки сохранения логотипа
     */
    public boolean save(Team team, MultipartFile logo, User user) throws IOException {
        saveLogo(team,logo);
        team.setOrganizer(user);
        createGroupChat(team);
        repository.save(team);
        return true;
    }

    /**
     * Метод, который обновляет существующую команду
     * @param team - обновляемая команда
     * @return - Булево значение, true - если команда удачно обновлена
     */
    public boolean update(Team team){
        repository.save(team);
        return true;
    }

    /**
     * Метод, который сохраняет логотип команды
     * @param team - Команда, чей логотип мы сохраняем
     * @param file - Файл с изображением логотипа
     * @throws IOException - Исключение, которое может быть выброшено в случае ошибки сохранения логотипа
     */
    public void saveLogo(Team team, MultipartFile file) throws IOException{
        String resultFilename = imageUtils.saveFile(file);
        if(!"".equals(resultFilename)){
            team.setLogoFilename(resultFilename);
            repository.save(team);
        }
    }

    /**
     * Метод, который обрезает логотип и сохраняет его
     * @param team - Команда, чей логотип мы сохраняем
     * @param file - Файл с изображением логотипа
     * @param x - X координата начала обрезки
     * @param y - Y координата начала обрезки
     * @param width - Ширина обрезанного изображения
     * @param height - Высота обрезанного изображения
     * @throws IOException - Исключение, которое может быть выброшено в случае ошибки сохранения логотипа
     */
    public void cropAndSaveLogo(Team team, MultipartFile file, Integer x, Integer y, Integer width, Integer height) throws IOException{
        String resultFilename = imageUtils.cropAndSaveImage(file, x, y, width, height);
        if(!"".equals(resultFilename)){
            team.setLogoFilename(resultFilename);
            repository.save(team);
        }
    }

    /**
     * Метод, который создает Групповой чат (GroupRoom) команды
     * @param team - Команда, для которой создается групповой чат
     */
    public void createGroupChat(Team team) {
        String groupChatId = UUID.randomUUID().toString();
        team.setGroupChatId(groupChatId);
        String adminId = team.getOrganizer().getId().toString();
        roomService.createGroupRoomWithGivenChatId(adminId, Set.of(adminId), team.getTeamName(), groupChatId);
    }

    /**
     * Метод, который добавляет в команду нового пользователя
     * @param team - Команда, в которую вступает пользователь
     * @param user - Вступающий пользователь
     */
    public void joinTeam(Team team, User user) {
        String userId = user.getId().toString();
        team.getTeammates().add(user);
        user.getTeams().add(team);
        roomService.addGroupMember(team.getGroupChatId(), userId);
        update(team);
        securityUtils.updateContext(user);
    }

    /**
     * Метод, который убирает из команды пользователя, который в ней уже состоит
     * @param team - Команда, из которой убирается пользователь
     * @param user - Убираемый пользователь
     */
    public void quitTeam(Team team, User user) {
        String userId = user.getId().toString();
        team.getTeammates().remove(user);
        user.getTeams().remove(team);
        roomService.removeGroupMember(team.getGroupChatId(), userId);
        update(team);
        securityUtils.updateContext(user);
    }

    public void gradeTeam(RegisteredTeam registeredTeam, Result result, Competition competition) {
        Team team = repository.findById(registeredTeam.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        team.getStatistics().add(new Statistics(result, competition.getId()));
        repository.save(team);
    }
}
