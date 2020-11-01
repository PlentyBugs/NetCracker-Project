package org.netcracker.project.service;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.TeamRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.Multipart;
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
    }

    public boolean save(Team team, Multipart logo, User user) throws IOException {  //need to write body

        return true;
    }

    public boolean update(Team team){   //body
        return true;
    }
    private
}
