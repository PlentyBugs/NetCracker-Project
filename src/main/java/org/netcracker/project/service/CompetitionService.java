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
import java.time.LocalDateTime;

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
        String safe = dateUtil.getDATE_PATTERN_SAFE();
        if (filter.matches("after" + safe)) {
            return compileFilter(pageable, filter, "after");
        } else if (filter.matches("before" + safe)) {
            return compileFilter(pageable, filter, "before");
        } else if (filter.matches("equals" + safe)) {
            return compileFilter(pageable, filter, "equals");
        }
        return getPage(pageable);
    }

    private Page<Competition> compileFilter(Pageable pageable, String filter, String command) {
        LocalDateTime localDateTime = LocalDateTime.parse(filter.replaceFirst(command, ""), dateUtil.getFormatter());
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

    public DateCallback parseDateFromForm(String formDate) {
        if ((formDate = formDate.replaceFirst("T", " ")).matches("2[0-9][2-9][0-9]-(1[0-2]|0[0-9])-([0-2][0-9]|3[0-1])\\s(2[0-4]|[01][0-9]):([0-5][0-9]|60)")) {
            LocalDateTime parsed = LocalDateTime.parse(formDate, dateUtil.getFormDateFormatter());
            return new DateCallback(parsed, true);
        }
        return new DateCallback(null, false);
    }
}
