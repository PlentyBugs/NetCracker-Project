package org.netcracker.project.repository;

import org.netcracker.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Метод используется для получения пользователя по его нику
     * @param username Ник пользователя
     * @return Пользователь с ником <i>username</i> если был найден и null в ином случае
     */
    User findByUsername(String username);

    /**
     * Метод используется для получения Optional, содержащего User, по его Id
     * @param id Id пользователя
     * @return Optional с User если пользователь с таким Id был найден и пустой Optional в ином случае
     */
    Optional<User> findById(Long id);

    /**
     * Метод используется при активации пользователя, чтобы можно было найти его по активационному коду
     * @param activationCode Активационный код пользователя
     * @return Пользователь с активационным кодом <i>activationCode</i> если был найден и null в ином случае
     */
    User findByActivationCode(String activationCode);
}