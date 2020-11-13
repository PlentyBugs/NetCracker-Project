package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.TeamFilter;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository repository;
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

    public Team getOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
