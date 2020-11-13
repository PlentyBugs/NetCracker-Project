package org.netcracker.project.repository;

import org.netcracker.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    Optional<User> findById(Long id);
    User findByActivationCode(String activationCode);


}