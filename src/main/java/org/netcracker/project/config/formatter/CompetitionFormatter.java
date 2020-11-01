package org.netcracker.project.config.formatter;

import org.netcracker.project.model.Competition;
import org.netcracker.project.repository.CompetitionRepository;
import org.springframework.format.Formatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Component
public class CompetitionFormatter implements Formatter<Competition> {

    private final CompetitionRepository repository;

    public CompetitionFormatter(CompetitionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Competition parse(String s, Locale locale) throws ResponseStatusException {
        Optional<Competition> optionalCompetition = repository.findById(Long.parseLong(s));
        return optionalCompetition.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public String print(Competition competition, Locale locale) {
        return competition.getCompName();
    }
}
