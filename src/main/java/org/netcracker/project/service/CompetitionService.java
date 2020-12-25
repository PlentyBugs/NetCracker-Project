package org.netcracker.project.service;

import org.netcracker.project.filter.CompetitionFilter;
import org.netcracker.project.model.Competition;
import org.netcracker.project.model.RegisteredTeam;
import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.netcracker.project.util.callback.DateCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface CompetitionService {

   /**
    * Метод, который возвращает страницу соревнований по заданным настройкам страницы
    * @param pageable Объект Pageable с настройками страницы
    * @return Страница с соревнованиями с заданными настройками
    */
   Page<Competition> getPage(Pageable pageable);

   /**
    * Метод, который возвращает страницу соревнований по заданным настройкам страницы и удовлетворяющий всем фильтрам
    * @param pageable Объект Pageable с настройками страницы
    * @param filter Фильтры для соревнований
    * @return Страницы с соревнованиями с заданными настройками и удовлетворяющий всем фильтрам
    */
   Page<Competition> getPage(Pageable pageable, CompetitionFilter filter);

   /**
    * Метод, который сохраняет соревнование
    * @param competition Сохраняемое соревнование
    * @param title Лого соревнования
    * @param user Пользователь, который будет считаться организатором соревнования
    * @return Булево значение, true - если соревнование создалось без ошибок
    * @throws IOException - Исключение, которое может быть выброшено, если возникнет ошибка сохранения лого соревнования
    */
   boolean save(Competition competition, MultipartFile title, User user) throws IOException;

   /**
    * Метод, который обновляет соревнование
    * @param competition Обновляемое соревнование
    * @return Булево значение, true - если обновление прошло без ошибок
    */
   boolean update(Competition competition);

   /**
    * Метод, который вызывает метод DateUtil parseDateFromForm
    * Он парсит дату, которая была получена от клиента из формы
    * @param formDate Строка, содержащая дату, полученную от клиента из формы
    * @return Коллбек, в котором хранится 2 значения: объект даты и булево значение,
    *                если булево значение true, то парсинг прошел без проблем и можно брать объект,
    *                иначе произошла какая-то ошибка и объект равен null
    */
   DateCallback parseDateFromForm(String formDate);

   /**
    * Метод, который возвращает все соревнования по пользователю для календаря
    * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
    * @param user Пользователь, чьи соревнования будут показаны
    * @param startDate Дата начала месяца из календаря
    * @return Список соревнований по пользователю за определенный месяц
    */
   List<Competition> getAllByUserCalendar(User user, String startDate);

   /**
    * Метод, который возвращает все соревнования по команде для календаря
    * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
    * @param team Команда, чьи соревнования будут показаны
    * @param startDate Дата начала месяца из календаря
    * @return Список соревнований по команде за определенный месяц
    */
   List<Competition> getAllByTeamCalendar(Team team, String startDate);

   /**
    * Метод, который возвращает все соревнования для календаря
    * Это накладывает определенные временные ограничения по выборке, чтобы не брать все соревнования
    * @param startDate Дата начала месяца из календаря
    * @return Список соревнований за определенный месяц
    */
   List<Competition> getAllCalendar(String startDate);

   /**
    * Метод, который регистрирует команду на участие в соревновании
    * @param competition Соревнование, в которое происходит запись
    * @param team Команда, которая записывается на соревнование
    */
   void addTeam(Competition competition, Team team);

   /**
    * Метод, который отписывает команду от участия в соревновании
    * @param competition Соревнование, от которого отписывается команда
    * @param user Команда, которая отписывается от соревнования
    */
   void removeTeamByUser(Competition competition, User user);

   /**
    * Метод, который возвращает все законченные соревнования, где указанный пользователь является организатором
    * @param user Пользователь, который является организатором соревнований
    * @return Список законченных соревнований, для который указанный пользователь - организатор
    */
   List<Competition> getAllEndedCompetitions(User user);

   /**
    * Метод, который возвращает все действующие соревнования, где указанный пользователь является организатором
    * @param user Пользователь, который является организатором соревнований
    * @return Список действующих соревнований, для который указанный пользователь - организатор
    */
   List<Competition> getAllActingCompetitions(User user);

   /**
    * Метод, в котором идет оценивание всех команд, участвующих в соревновании
    * @param competition Соревнование, команды которой оцениваются
    * @param winner Победитель соревнования
    * @param second Команда, занявшая второе место
    * @param third Команда, занявшая третье место
    * @param spotted Множество команд, которые были замечены спонсорами
    */
   void gradeCompetition (
            Competition competition,
            RegisteredTeam winner,
            RegisteredTeam second,
            RegisteredTeam third,
            Set<RegisteredTeam> spotted
   );

   /**
    * Метод, который создает групповой чат для соревнования
    * @param competition Соревнование, для которого будет создан групповой чат
    */
   void createGroupChat(Competition competition);
}
