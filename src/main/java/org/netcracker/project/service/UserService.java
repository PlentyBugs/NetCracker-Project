package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.repository.UserRepository;
import org.netcracker.project.util.ImageUtils;
import org.netcracker.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final UserRepository repository;
    private final MailService mailService;
    private final ImageUtils imageUtils;

    @Value("${hostname}")
    private String hostname;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);

        if (user == null) throw new UsernameNotFoundException("User not found");

        return user;
    }

    public boolean create(User user, Set<Role> roles) {
        user.setRoles(roles);
        return create(user);
    }

    public boolean create(User user) {
        User userFromDB = repository.findByUsername(user.getUsername());

        if (userFromDB != null) return false;

        user.setActive(false);
        if (user.getRoles() == null) {
            user.setRoles(Collections.singleton(Role.USER));
        } else {
            user.getRoles().add(Role.USER);
        }
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        repository.save(user);

        sendMessage(user);

        return true;
    }

    public boolean update(User user) {
        repository.save(user);
        return true;
    }

    private void sendMessage(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to NetHacker! Please, visit next link to activate your account: http://%s/registration/activate/%s"
                    , user.getUsername(), hostname, user.getActivationCode()
            );
            mailService.send(user.getEmail(), "Activation Code", message);
        }
    }

    public boolean activate(String code) {
        User user = repository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);

        repository.save(user);

        return true;
    }

    private void saveAvatar(@Valid User user, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = imageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            user.setAvatarFilename(resultFilename);
        }
    }

    public boolean updateUser(User authUser, User user, String password2) {
        String encodedNewPassword = passwordEncoder.encode(user.getPassword());
        if (!BCrypt.checkpw(password2, authUser.getPassword()) && user.getPassword().equals(password2)) {
            user.setPassword(encodedNewPassword);
        }
        securityUtils.updateContext(repository.save(user));
        return true;
    }

    public boolean deleteUser(User user) {
        user.setActive(false);
        repository.save(user);
        return true;
    }

    public String findUsernameById(Long id) {
        return repository.findById(id).map(User::getUsername).orElse("");
    }

    public String findFullNameAndUsernameById(Long id) {
        return repository.findById(id).map(u -> u.getSurname() + " " + u.getName() + " (" + u.getUsername() + ")").orElse("");
    }

    public SimpleUser findSimpleUserById(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return SimpleUser
                .builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .build();
    }
}
