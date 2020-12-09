package org.netcracker.project.repository;

import org.netcracker.project.model.Competition;
import org.netcracker.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition,Long> {

    /**
     * Метод использующийся для получения соревнования по его названию
     * @param compName Название соревнования
     * @return Соревнование с данным названием
     */
    Competition findByCompName(String compName);

    /**
     * Метод использующийся для получения страницы соревнований с заданными настройками Pageable
     * @param pageable Объект Pageable с информацией о странице
     * @return Страница с соревнованиями для заданного Pageable
     */
    Page<Competition> findAll(Pageable pageable);

    /**
     * Метод используется для получения страницы соревнований с заданными настройками Pageable.
     * Кроме того, название соревнования или его описание должно содержать строку <i>search</i>. От регистра не зависит
     * @param pageable Объект Pageable с информацией о странице
     * @param search Строка, которую должны содержать описание соревнования и/или его название. От регистра не зависит
     * @return Страница с соревнованиями, содержащими <i>search</i> в описании и/или названии, для заданного Pageable
     */
    @Query("from Competition c where lower(c.description) like lower(:search) or lower(c.compName) like lower(:search)")
    Page<Competition> findAllBySearch(Pageable pageable, String search);

    /**
     * Метод, который используется для получения страницы соревнований, которые начались после <i>startDate</i>
     * с заданными настройками Pageable
     * @param pageable Объект Pageable с информацией о странице
     * @param startDate Дата, после которой начались соревнования
     * @return Страница с соревнованиями, которые начались после <i>startDate</i>, для заданного Pageable
     */
    Page<Competition> findAllByStartDateAfter(Pageable pageable, LocalDateTime startDate);

    /**
     * Метод, который используется для получения страницы соревнований, которые начались до <i>startDate</i>
     * @param pageable Объект Pageable с информацией о странице
     * @param startDate Дата, до которой начались соревнования
     * @return Страница с соревнованиями, которые начались до <i>startDate</i>, для заданного Pageable
     */
    Page<Competition> findAllByStartDateBefore(Pageable pageable, LocalDateTime startDate);

    /**
     * Метод, который используется для получения страницы соревнований, которые начались в <i>startDate</i> и содержат
     * в названии и/или описании строку <i>search</i> вне зависимости от регистра, для заданного Pageable
     * @param pageable Объект Pageable с информацией о странице
     * @param startDate Дата, в которую начались соревнования
     * @param search Строка, которую содержат названия и/или описания соревнований без учета регистра.
     * @return Страница с соревнованиями, которые начались в <i>startDate</i> и содержат в названии и/или описании
     * строку <i>search</i>, для заданного Pageable
     */
    @Query("from Competition c where c.startDate = :startDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByStartDateEquals(
            Pageable pageable,
            LocalDateTime startDate,
            String search
    );

    /**
     * Метод, который используется для получения страницы соревнований, которые закончились в <i>endDate</i> и содержат
     * в названии и/или описании строку <i>search</i> вне зависимости от регистра, для заданного Pageable
     * @param pageable Объект Pageable с информацией о странице
     * @param endDate Дата, в которую закончились соревнования
     * @param search Строка, которую содержат названия и/или описания соревнований без учета регистра.
     * @return Страница с соревнованиями, которые закончились в <i>endDate</i> и содержат в названии и/или описании
     * строку <i>search</i>, для заданного Pageable
     */
    @Query("from Competition c where c.endDate = :endDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByEndDateEquals(
            Pageable pageable,
            LocalDateTime endDate,
            String search
    );

    /**
     * Метод, который используется для получения страницы соревнований, которые начались в <i>startDate</i>,
     * закончились в <i>endDate</i> и содержат в названии и/или описании строку <i>search</i>
     * вне зависимости от регистра, для заданного Pageable
     * @param pageable Объект Pageable с информацией о странице
     * @param startDate Дата, в которую начались соревнования
     * @param endDate Дата, в которую закончились соревнования
     * @param search Строка, которую содержат названия и/или описания соревнований без учета регистра.
     * @return Страница с соревнованиями, которые начались в <i>startDate</i>, закончились в <i>endDate</i>
     * и содержат в названии и/или описании строку <i>search</i>, для заданного Pageable
     */
    @Query("from Competition c where c.startDate = :startDate and c.endDate = :endDate and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByStartDateEqualsAndEndDateEquals(
            Pageable pageable,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String search
    );

    /**
     * Метод, который используется для получения страницы соревнований, которые укладываются в указанные сроки и содержат
     * в названии и/или описании строку <i>search</i> вне зависимости от регистра, для заданного Pageable.
     * <b>Ограничения по датам:</b>
     * <i>afterStart</i> <= startDate <= <i>beforeStart</i>
     * <i>afterEnd</i> <= endDate <= <i>beforeEnd</i>
     * @param pageable Объект Pageable с информацией о странице
     * @param beforeStart Дата, до которой должно начинаться соревнование
     * @param afterStart Дата, после которой должно начинаться соревнование
     * @param beforeEnd Дата, до которой должно закончиться соревнование
     * @param afterEnd Дата, после которой должно закончиться соревнование
     * @param search Строка, которую содержат названия и/или описания соревнований без учета регистра.
     * @return Страница с соревнованиями, которые укладываются в указанные сроки и содержат в названии и/или
     * описании строку <i>search</i> вне зависимости от регистра, для заданного Pageable
     */
    @Query("from Competition c where c.startDate <= :beforeStart and c.startDate >= :afterStart and c.endDate <= :beforeEnd and c.endDate >= :afterEnd and (lower(c.description) like lower(:search) or lower(c.compName) like lower(:search))")
    Page<Competition> findAllByBounds(
            Pageable pageable,
            LocalDateTime beforeStart,
            LocalDateTime afterStart,
            LocalDateTime beforeEnd,
            LocalDateTime afterEnd,
            String search
    );

    /**
     * Метод, который используется для получения списка соревнований для команды в заданные сроки.
     * Конкретно этот метод нужен для календаря, т.е. тут предполагаются сроки: начало месяца и конец месяца +- несколько дней
     * @param teamId Id команды, чьи соревнования мы запрашиваем
     * @param startDate Дата, после которой начинается соревнование
     * @param endMonthDate Дата, до которой начинается соревнование
     * @return Список соревнований для заданной команды, которые укладываются в заданные сроки
     */
    @Query("from Competition c where exists (select t.id from c.teams as t where :teamId = t.id) and (c.startDate >= :startDate or c.startDate <= :endMonthDate)")
    List<Competition> findAllByTeamCalendar(
            Long teamId,
            LocalDateTime startDate,
            LocalDateTime endMonthDate
    );

    /**
     * Метод, который используется для получения списка соревнований для пользователя в заданные сроки.
     * Конкретно этот метод нужен для календаря, т.е. тут предполагаются сроки: начало месяца и конец месяца +- несколько дней
     * @param user Пользователь, чьи соревнования запрашиваем
     * @param startDate Дата, после которой начинается соревнование
     * @param endMonthDate Дата, до которой начинается соревнование
     * @return Список соревнований для заданного пользователя, которые укладываются в заданные сроки
     */
    @Query("from Competition c where exists (select t.id from c.teams as t where :user member of t.teammates) and (c.startDate >= :startDate or c.startDate <= :endMonthDate)")
    List<Competition> findAllByUserCalendar(
            User user,
            LocalDateTime startDate,
            LocalDateTime endMonthDate
    );

    /**
     * Метод, который используется для получения списка всех соревнований в заданные сроки.
     * Конкретно этот метод нужен для календаря, т.е. тут предполагаются сроки: начало месяца и конец месяца +- несколько дней
     * @param startDate Дата, после которой начинается соревнование
     * @param endMonthDate Дата, до которой начинается соревнование
     * @return Список всех соревнований, которые укладываются в заданные сроки
     */
    @Query("from Competition c where c.startDate >= :startDate or c.startDate <= :endMonthDate")
    List<Competition> findAllCalendar(LocalDateTime startDate, LocalDateTime endMonthDate);

    /**
     * Метод, который используется для получения архива соревнований организатора.
     * Т.е. тех соревнований, которые создал указанный пользователь, и которые уже закончились.
     * @param endDate Дата, после которой соревнование считается законченной. Обычно должна использоваться текущая дата.
     * @param user Пользователь, который организовал соревнования
     * @return Список соревнований, где заданный пользователь является организатором, и которые уже закончились на момент
     * даты <i>endDate</i>
     */
    @Query("from Competition  c where c.endDate < :endDate and (c.organizer = :user)")
    List<Competition> getArchiveByUser(LocalDateTime endDate, User user);

    /**
     * Метод, который используется для получения все еще активных соревнований организатора
     * Т.е. тех соревнований, которые создал указанный пользователь, и которые еще не закончились.
     * @param endDate Дата, по которой проверяется активность соревнования
     * @param user Пользователь, который организовал соревнования
     * @return Список соревнований, где заданный пользователь является организатором, и которые еще не закончились на
     * момент даты <i>endDate</i>
     */
    @Query("from Competition  c where c.endDate >= :endDate and (c.organizer = :user)")
    List<Competition> getRunningCompByUser(LocalDateTime endDate,User user);
}
