package org.netcracker.project.repository;

import org.netcracker.project.model.RegisteredTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredTeamRepository extends JpaRepository<RegisteredTeam, Long> {

    /**
     * Метод, который используется для получения Зарегистрированной на соревновании команды по ее названию
     * @param teamName - Название зарегистрированной/оригинальной команды. Подойдет любое, т.к. название команд уникально,
     *                 а зарегистрированная команда создается как прототип оригинальной (Team), поэтому их названия одинаковы
     * @return Зарегистрированная команда с данным названием, если была найдена, и null в ином случае
     */
    RegisteredTeam findByTeamName(String teamName);
}
