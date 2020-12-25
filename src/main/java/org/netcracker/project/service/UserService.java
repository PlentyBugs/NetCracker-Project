package org.netcracker.project.service;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.netcracker.project.model.dto.SimpleUser;
import org.netcracker.project.model.enums.Result;
import org.netcracker.project.model.enums.Role;
import org.netcracker.project.model.enums.TeamRole;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

    /**
     * Метод, принимающий ник пользователя и возвращающий юзера, если таковой найден, иначе бросает исключение
     *
     * @param username Ник пользователя
     * @return Классы, наследующие UserDetails, в нашем случае User
     * @throws UsernameNotFoundException - исключение, которое будет выброшено, в случае, если пользователь не будет найден
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Метод, который создает пользователя с заданными колями
     *
     * @param user  Новый пользователь
     * @param roles Множество ролей
     * @return Булево значение, true - если пользователь создан и сохранен, false - если что-то пошло не так или пользователь уже существует
     */
    boolean create(User user, Set<Role> roles);

    /**
     * Метод, который создает пользователя
     *
     * @param user Новый пользователь
     * @return Булево значение, true - если пользователь создан и сохранен, false - если что-то пошло не так или пользователь уже существует
     */
    boolean create(User user);

    /**
     * Метод, который обновляет существующего пользователя
     *
     * @param user Существующий пользователь
     * @return Булево значение, true - если успешно обновлен, false - если по какой-то причине не сохранился
     */
    boolean update(User user);

    /**
     * Метод, который производит активацию пользователя по активационному коду
     *
     * @param code Активационный код
     * @return Булево значение, true - если успешно активирован, false - если пользователя не существует
     */
    boolean activate(String code);

    /**
     * Метод, который сохраняет аватар пользователя
     *
     * @param user Пользователь, аватар которого мы сохраняем
     * @param file Файл с картинкой
     * @throws IOException - Исключение в случае ошибки сохранения аватара
     */
    void saveAvatar(@Valid User user, @RequestParam("avatar") MultipartFile file) throws IOException;

    /**
     * Метод, который обрезает аватар и сохраняет его
     *
     * @param user   Пользователь, чей аватар мы сохраняем
     * @param file   Файл с изображением логотипа
     * @param x      X координата начала обрезки
     * @param y      Y координата начала обрезки
     * @param width  Ширина обрезанного изображения
     * @param height Высота обрезанного изображения
     * @throws IOException - Исключение, которое может быть выброшено в случае ошибки сохранения логотипа
     */
    void cropAndSaveAvatar(User user, MultipartFile file, Integer x, Integer y, Integer width, Integer height) throws IOException;

    /**
     * Метод для обновления данных пользователя, требующий подтверждение пароля пользователя
     *
     * @param authUser  Пользователь из контекста, который делает запрос на обновление
     * @param user      Пользователь с новыми данными
     * @param password2 Повторный пароль для подтверждения
     * @return Булево значение, true - если пользователь удачно обновлен
     */
    boolean updateUser(User authUser, User user, String password2);

    /**
     * Метод для удаления пользователя, а точнее для его деактивации, чтобы все связанные с ним объекты могли на него ссылаться.
     * Однако, в аккаунт больше нельзя будет зайти и пользоваться им.
     *
     * @param user Удаляемый пользователь
     * @return Булево значение, true- если удаление прошло успешно
     */
    boolean deleteUser(User user);

    /**
     * Метод, который находит ник пользователя по его Id
     *
     * @param id Id пользователя
     * @return Ник пользователя, если таковой найден и пустая строка в ином случае
     */
    String findUsernameById(Long id);

    /**
     * Вспомогательный метод, который возвращает фамилию, имя и ник пользователя в формате Ф И (Н)
     *
     * @param id Id пользователя
     * @return "Фамилия Имя (Ник)" пользователя, если таковой найден и "" в ином случае
     */
    String findFullNameAndUsernameById(Long id);

    /**
     * Метод, который возвращает DTO SimpleUser, содержащий Id, Имя, Фамилию и Ник
     *
     * @param userId Id пользователя
     * @return DTO SimpleUser: id, name, surname, username
     */
    SimpleUser findSimpleUserById(Long userId);

    /**
     * Метод, который возвращает список всех пользователей
     *
     * @return List содержащий всех пользователей
     */
    List<User> findAll();

    /**
     * Метод, который используется для изменения командных ролей пользователя
     *
     * @param user  Пользователь, чьи командные роли меняют
     * @param roles Множество командных ролей, на которые будет заменено текущее множество командных ролей пользователя
     */
    void updateUserRoles(User user, Set<TeamRole> roles);

    /**
     * Метод, который используется для оценки участия пользователя в соревновании
     *
     * @param user        Оцениваемый пользователь
     * @param result      Результат, который будет присужден по итогам соревнования
     * @param competition Соревнование, за которое оценивается пользователь
     */
    void gradeUser(User user, Result result, Competition competition);

    /**
     * Метод, который используется для получения списка всех пользователей в упрощенном формате SimpleUser
     *
     * @return Множество всех пользователей в формате SimpleUser
     */
    Set<SimpleUser> findAllSimpleUsers();
}
