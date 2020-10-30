package org.netcracker.project.service;

import lombok.extern.log4j.Log4j2;
import org.netcracker.project.model.User;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.repository.UserRepository;
import org.netcracker.project.util.ImageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${hostname}")
    private String hostname;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

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

        log.info(user.getUsername() + " is registered!");

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
            log.info("An attempt was made to activate a non-existent user");
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);

        repository.save(user);

        log.info(user.getUsername() + " activated account!");

        return true;
    }

    private void saveAvatar(@Valid User user, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = ImageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            user.setAvatarFilename(resultFilename);
        }
    }
}
