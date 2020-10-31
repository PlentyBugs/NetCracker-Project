package org.netcracker.project.service;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.CompetitionRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class CompetitionService {

    private final CompetitionRepository repository;
    private final String DATE_PATTERN_SAFE;
    private final DateTimeFormatter formatter;
    private final ImageUtils imageUtils;

    public CompetitionService(CompetitionRepository repository, ImageUtils imageUtils) {
        this.repository = repository;
        String DATE_PATTERN = "dd.MM.yyyy HH:mm";
        DATE_PATTERN_SAFE = Pattern.quote(DATE_PATTERN);
        formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        this.imageUtils = imageUtils;
    }

    public Page<Competition> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Competition> getPage(Pageable pageable, String filter) {
        if (filter.matches("after" + DATE_PATTERN_SAFE)) {
            return compileFilter(pageable, filter, "after");
        } else if (filter.matches("before" + DATE_PATTERN_SAFE)) {
            return compileFilter(pageable, filter, "before");
        } else if (filter.matches("equals" + DATE_PATTERN_SAFE)) {
            return compileFilter(pageable, filter, "equals");
        }
        return getPage(pageable);
    }

    private Page<Competition> compileFilter(Pageable pageable, String filter, String command) {
        LocalDateTime localDateTime = LocalDateTime.parse(filter.replaceFirst(command, ""), formatter);
        return repository.findAllByStartDateBefore(pageable, localDateTime);
    }

    public boolean save(Competition competition, MultipartFile title, User user) throws IOException {
        competition.setOrganizer(user);
        saveTitle(competition, title);
        repository.save(competition);
        return true;
    }

    public boolean update(Competition competition) {
        repository.save(competition);
        return true;
    }

    private void saveTitle(@Valid Competition competition, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = imageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            competition.setTitleFilename(resultFilename);
        }
    }
}
