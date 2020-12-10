package org.netcracker.project.config.formatter;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.repository.UserRepository;
import org.springframework.format.Formatter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFormatter implements Formatter<User> {

    private final UserRepository repository;

    @Override
    public User parse(String s, Locale locale) throws ResponseStatusException {
        if (s.length() > 2048) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<User> optionalUser = repository.findById(Long.parseLong(s));
        return optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public String print(User User, Locale locale) {
        return User.getUsername();
    }
}
