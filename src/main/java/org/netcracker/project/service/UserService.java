package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.model.enums.TeamRole;
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
import java.util.List;
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

    /**
     * Метод, принимающий ник пользователя и возвращающий юзера, если таковой найден, иначе бросает исключение
     * @param username - Ник пользователя
     * @return - Классы, наследующие UserDetails, в нашем случае User
     * @throws UsernameNotFoundException - исключение, которое будет выброшено, в случае, если пользователь не будет найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);

        if (user == null) throw new UsernameNotFoundException("User not found");

        return user;
    }

    /**
     * Метод, который создает пользователя с заданными колями
     * @param user - Новый пользователь
     * @param roles - Множество ролей
     * @return - Булево значение, true - если пользователь создан и сохранен, false - если что-то пошло не так или пользователь уже существует
     */
    public boolean create(User user, Set<Role> roles) {
        user.setRoles(roles);
        return create(user);
    }

    /**
     * Метод, который создает пользователя
     * @param user - Новый пользователь
     * @return - Булево значение, true - если пользователь создан и сохранен, false - если что-то пошло не так или пользователь уже существует
     */
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

    /**
     * Метод, который обновляет существующего пользователя
     * @param user - Существующий пользователь
     * @return - Булево значение, true - если успешно обновлен, false - если по какой-то причине не сохранился
     */
    public boolean update(User user) {
        repository.save(user);
        return true;
    }

    /**
     * Метод, который для данного пользователя отправляет регистрационное сообщение на почту
     * @param user - Пользователь, которому будет отправлено сообщение
     */
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

    /**
     * Метод, который производит активацию пользователя по активационному коду
     * @param code - Активационный код
     * @return - Булево значение, true - если успешно активирован, false - если пользователя не существует
     */
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

    /**
     * Метод, который сохраняет аватар пользователя
     * @param user - Пользователь, аватар которого мы сохраняем
     * @param file - Файл с картинкой
     * @throws IOException - Исключение в случае ошибки сохранения аватара
     */
    public void saveAvatar(@Valid User user, @RequestParam("avatar") MultipartFile file) throws IOException {
        String resultFilename = imageUtils.saveFile(file);
        if (!"".equals(resultFilename)) {
            user.setAvatarFilename(resultFilename);
            repository.save(user);
            securityUtils.updateContext(user);
        }
    }

    /**
     * Метод, который обрезает аватар и сохраняет его
     * @param user - Пользователь, чей аватар мы сохраняем
     * @param file - Файл с изображением логотипа
     * @param x - X координата начала обрезки
     * @param y - Y координата начала обрезки
     * @param width - Ширина обрезанного изображения
     * @param height - Высота обрезанного изображения
     * @throws IOException - Исключение, которое может быть выброшено в случае ошибки сохранения логотипа
     */
    public void cropAndSaveAvatar(User user, MultipartFile file, Integer x, Integer y, Integer width, Integer height) throws IOException{
        String resultFilename = imageUtils.cropAndSaveImage(file, x, y, width, height);
        if(!"".equals(resultFilename)){
            user.setAvatarFilename(resultFilename);
            repository.save(user);
            securityUtils.updateContext(user);
        }
    }

    /**
     * Метод для обновления данных пользователя, требующий подтверждение пароля пользователя
     * @param authUser - Пользователь из контекста, который делает запрос на обновление
     * @param user - Пользователь с новыми данными
     * @param password2 - Повторный пароль для подтверждения
     * @return - Булево значение, true - если пользователь удачно обновлен
     */
    public boolean updateUser(User authUser, User user, String password2) {
        String encodedNewPassword = passwordEncoder.encode(user.getPassword());
        if (!BCrypt.checkpw(password2, authUser.getPassword()) && user.getPassword().equals(password2)) {
            user.setPassword(encodedNewPassword);
        }
        securityUtils.updateContext(repository.save(user));
        return true;
    }

    /**
     * Метод для удаления пользователя, а точнее для его деактивации, чтобы все связанные с ним объекты могли на него ссылаться.
     * Однако, в аккаунт больше нельзя будет зайти и пользоваться им.
     * @param user - Удаляемый пользователь
     * @return - Булево значение, true- если удаление прошло успешно
     */
    public boolean deleteUser(User user) {
        user.setActive(false);
        repository.save(user);
        return true;
    }

    /**
     * Метод, который находит ник пользователя по его Id
     * @param id - Id пользователя
     * @return - Ник пользователя, если таковой найден и пустая строка в ином случае
     */
    public String findUsernameById(Long id) {
        return repository.findById(id).map(User::getUsername).orElse("");
    }

    /**
     * Вспомогательный метод, который возвращает фамилию, имя и ник пользователя в формате Ф И (Н)
     * @param id - Id пользователя
     * @return - "Фамилия Имя (Ник)" пользователя, если таковой найден и "" в ином случае
     */
    public String findFullNameAndUsernameById(Long id) {
        return repository.findById(id).map(u -> u.getSurname() + " " + u.getName() + " (" + u.getUsername() + ")").orElse("");
    }

    /**
     * Метод, который возвращает DTO SimpleUser, содержащий Id, Имя, Фамилию и Ник
     * @param userId - Id пользователя
     * @return - DTO SimpleUser: id, name, surname, username
     */
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

    /**
     * Метод, который возвращает список всех пользователей
     * @return - List содержащий всех пользователей
     */
    public List<User> findAll() {
        return repository.findAll();
    }

    public void updateUserRoles(User user, Set<TeamRole> roles) {
        user.setTeamRoles(roles);
        repository.save(user);
        securityUtils.updateContext(user);
    }

    public void gradeUser(User user, Result result, Competition competition) {
        user.getStatistics().put(result, competition);
        repository.save(user);
    }
}
