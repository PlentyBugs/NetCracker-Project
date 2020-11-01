package org.netcracker.project.service;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.validation.Valid;
import java.io.IOException;

@Service
public class TeamService {
    private final TeamRepository repository;
    private final ImageUtils imageUtils;

    public TeamService(TeamRepository repository,ImageUtils imageUtils)
    {
        this.repository=repository;
        this.imageUtils=imageUtils;
    }
    public Page<Team> getPage(Pageable pageable){return repository.findAll(pageable);}
    public Page<Team> getPage(Pageable pageable, String filter){    //need to write body
        return getPage(pageable);
    }  //to write filter

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
    private void saveLogo(@Valid Team team, @RequestParam("avatar") MultipartFile file)throws IOException{
        String resultFilename=imageUtils.saveFile(file);
        if(!"".equals(resultFilename)){
            team.setLogoFilename(resultFilename);
        }
    }
}