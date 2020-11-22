package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final SecurityUtils securityUtils;
    private final TeamRepository repository;
    private final RoomService roomService;
    private final ImageUtils imageUtils;

    public Page<Team> getPage(Pageable pageable){return repository.findAll(pageable);}

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

    public boolean save(Team team, MultipartFile logo, User user) throws IOException {
        saveLogo(team,logo);
        team.setOrganizer(user);
        createGroupChat(team);
        repository.save(team);
        return true;
    }

    public boolean update(Team team){
        repository.save(team);
        return true;
    }

    private void saveLogo(@Valid Team team, @RequestParam("avatar") MultipartFile file) throws IOException{
        String resultFilename=imageUtils.saveFile(file);
        if(!"".equals(resultFilename)){
            team.setLogoFilename(resultFilename);
        }
    }

    public void createGroupChat(Team team) {
        String groupChatId = UUID.randomUUID().toString();
        team.setGroupChatId(groupChatId);
        String adminId = team.getOrganizer().getId().toString();
        roomService.createGroupRoomWithGivenChatId(adminId, Set.of(adminId), team.getTeamName(), groupChatId);
    }

    public void joinTeam(Team team, User user) {
        String userId = user.getId().toString();
        team.getTeammates().add(user);
        user.getTeams().add(team);
        roomService.addGroupMember(team.getGroupChatId(), userId);
        update(team);
        securityUtils.updateContext(user);
    }

    public void quitTeam(Team team, User user) {
        String userId = user.getId().toString();
        team.getTeammates().remove(user);
        user.getTeams().remove(team);
        roomService.removeGroupMember(team.getGroupChatId(), userId);
        update(team);
        securityUtils.updateContext(user);
    }
}
