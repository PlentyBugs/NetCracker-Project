package org.netcracker.project.config.formatter;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.repository.RegisteredTeamRepository;
import org.springframework.format.Formatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegisteredTeamFormatter implements Formatter<RegisteredTeam> {

    private final RegisteredTeamRepository repository;

    @Override
    public RegisteredTeam parse(String s, Locale locale) throws ResponseStatusException {
        if (s.length() > 2048) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<RegisteredTeam> optionalTeam = repository.findById(Long.parseLong(s));
        return optionalTeam.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public String print(RegisteredTeam Team, Locale locale) {
        return Team.getTeamName();
    }
}
