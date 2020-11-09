package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.Team;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository repository;
    private final ImageUtils imageUtils;
    private final DateUtil dateUtil;

    public List<Competition> getAll() {
        return repository.findAll();
    }

    public Page<Competition> getPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Competition> getPage(Pageable pageable, CompetitionFilter filter) {
        if (filter.isEqualsBoundsOn()) {
            if (filter.isEnableEqualsStart() && filter.isEnableEqualsEnd()) {
                return repository.findAllByStartDateEqualsAndEndDateEquals(pageable, filter.getEqualsStart(), filter.getEqualsEnd(), filter.getString());
            } else if (filter.isEnableEqualsStart()) {
                return repository.findAllByStartDateEquals(pageable, filter.getEqualsStart(), filter.getString());
            } else {
                return repository.findAllByEndDateEquals(pageable, filter.getEqualsEnd(), filter.getString());
            }
        }

        if (filter.isBoundsOn()) {
            if (!filter.isEnableBeforeStart() || filter.getBeforeStart() == null) filter.setBeforeStart(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableBeforeEnd() || filter.getBeforeEnd() == null) filter.setBeforeEnd(LocalDateTime.now().plusYears(100));
            if (!filter.isEnableAfterStart() || filter.getAfterStart() == null) filter.setAfterStart(LocalDateTime.now().minusYears(100));
            if (!filter.isEnableAfterEnd() || filter.getAfterEnd() == null) filter.setAfterEnd(LocalDateTime.now().minusYears(100));
            return repository.findAllByBounds(pageable, filter.getBeforeStart(), filter.getAfterStart(), filter.getBeforeEnd(), filter.getAfterEnd(), filter.getFormattedString());
        }

        return repository.findAllBySearch(pageable, filter.getFormattedString());
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

    public List<Competition> getAllByUser(User user) {
        return repository.findAllByUser(user);
    }

    public List<Competition> getAllByTeam(Team team) {
        return repository.findAllByTeam(team);
    }
}
