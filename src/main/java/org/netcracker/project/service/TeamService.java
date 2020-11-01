package org.netcracker.project.service;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.Multipart;
import java.io.IOException;

@Service
public class TeamService {

    public Page<Team> getPage(Pageable pageable, String filter){    //need to write body
        return getPage(pageable,filter);
    }

    public boolean save(Team team, Multipart logo, User user) throws IOException {  //need to write body
        return true;
    }

    public boolean update(Team team){   //body
        return true;
    }
}
