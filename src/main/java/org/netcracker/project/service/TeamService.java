package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository repository;
    private final ImageUtils imageUtils;

    public Page<Team> getPage(Pageable pageable){return repository.findAll(pageable);}

    public Page<Team> getPage(Pageable pageable, TeamFilter filter, User user) {
        if (filter.isRemoveEmpty()) {
            if (filter.getMinMembers() <= 0)
                filter.setMinMembers(1);
            if (filter.getMaxMembers() <= 0)
                filter.setMaxMembers(1);
        }
        if (filter.isNotInTheGroup()) {
            return repository.findAllWithFilterAndWithoutMe(
                    pageable,
                    filter.isMinMembersOn() ? filter.getMinMembers() : filter.isRemoveEmpty() ? 1 : -1,
                    filter.isMaxMembersOn() ? filter.getMaxMembers() : Integer.MAX_VALUE,
                    "%" + filter.getSearchName() + "%",
                    user
            );
        }
        return repository.findAllWithFilter(
                pageable,
                filter.isMinMembersOn() ? filter.getMinMembers() : filter.isRemoveEmpty() ? 1 : -1,
                filter.isMaxMembersOn() ? filter.getMaxMembers() : Integer.MAX_VALUE,
                "%" + filter.getSearchName() + "%"
        );
    }

    public boolean save(Team team, MultipartFile logo, User user) throws IOException {
        //...mb creator
        saveLogo(team,logo);
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
}
