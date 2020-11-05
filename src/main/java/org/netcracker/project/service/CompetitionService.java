package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.CompetitionRepository;
import org.netcracker.project.util.DateUtil;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.callback.DateCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository repository;
    private final ImageUtils imageUtils;
    private final DateUtil dateUtil;

    public Page<Competition> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Competition> getPage(Pageable pageable, String filter) {
        String pattern = dateUtil.getDATE_PATTERN();
        if (filter.matches("after" + pattern)) {
            return repository.findAllByStartDateAfter(pageable, dateUtil.compileFilter(filter, "after"));
        } else if (filter.matches("before" + pattern)) {
            return repository.findAllByStartDateBefore(pageable, dateUtil.compileFilter(filter, "before"));
        } else if (filter.matches("equals" + pattern)) {
            return repository.findAllByStartDateEquals(pageable, dateUtil.compileFilter(filter, "equals"));
        }
        return getPage(pageable);
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

    public DateCallback parseDateFromForm(String formDate) {
        return dateUtil.parseDateFromForm(formDate);
    }
}
