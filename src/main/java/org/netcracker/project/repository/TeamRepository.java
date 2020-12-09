package org.netcracker.project.repository;

import org.netcracker.project.model.Team;
import org.netcracker.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team,Long> {

    /**
     * Метод используется для получени из репозитория команды по её названию
     * @param teamName - Название команды
     * @return - Команда с названием <i>teamName</i> если была найдена и null в ином случае
     */
    Team findByTeamName(String teamName);

    /**
     * Метод используется для получения страницы с командами с определенными настройками в Pageable
     * @param pageable - Объект Pageable, содержащий данные о странице
     * @return - Страница с командами для данного Pageable
     */
    Page<Team> findAll(Pageable pageable);

    /**
     * Метод используется для получения страницы с командами с определенными настройками в Pageable.
     * Кроме того, выбираются команды согласно фильтрам, переданным в качестве аргументов функции.
     * Отличается от findAllWithFilter тем, что выдает команды, не содержащие указанного пользователя
     * @param pageable - Объект Pageable, содержащий данные о странице
     * @param min - Минимальное количество участников, включительно
     * @param max - Максимальное количество участников, включительно
     * @param name - Название команды
     * @param user - Пользователь, который не должен присутствовать в команде
     * @return - Страница с командами, которые удовлетворяют фильтрам и не содержат <i>user</i> в своем составе, для данного Pageable
     */
    @Query("from Team t where size(t.teammates) >= :min and size(t.teammates) <= :max and lower(t.teamName) like lower(:name) and not :user member of t.teammates")
    Page<Team> findAllWithFilterAndWithoutMe(Pageable pageable, int min, int max, String name, User user);

    /**
     * Метод используется для получения страницы с командами с определенными настройками в Pageable.
     * Кроме того, выбираются команды согласно фильтрам, переданным в качестве аргументов функции.
     * @param pageable - Объект Pageable, содержащий данные о странице
     * @param min - Минимальное количество участников, включительно
     * @param max - Максимальное количество участников, включительно
     * @param name - Название команды
     * @return - Страница с командами, которые удовлетворяют фильтрам, для данного Pageable
     */
    @Query("from Team t where size(t.teammates) >= :min and size(t.teammates) <= :max and lower(t.teamName) like lower(:name) ")
    Page<Team> findAllWithFilter(Pageable pageable, int min, int max, String name);
}
