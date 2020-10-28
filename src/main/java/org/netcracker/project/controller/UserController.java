package org.netcracker.project.controller;

import org.netcracker.project.model.User;
import org.netcracker.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@ControllerAdvice
@RestController
public class UserController extends ExceptionHandlerController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public User getUser(@RequestParam Long id){
        if (userRepository.findById(id).isPresent()){
            return userRepository.findById(id).get();
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User userForUpdate){
        Optional<User> optionalUser = userRepository.findById(userForUpdate.getId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setActivationCode(userForUpdate.getActivationCode());
            user.setActive(true);
            user.setAvatarFilename(userForUpdate.getAvatarFilename());
            user.setEmail(userForUpdate.getEmail());
            user.setName(userForUpdate.getName());
            user.setPassword(userForUpdate.getPassword());
            user.setRoles(userForUpdate.getRoles());
            user.setSecName(userForUpdate.getSecName());
            user.setSurname(userForUpdate.getSurname());
            user.setTeams(userForUpdate.getTeams());
            user.setUsername(userForUpdate.getUsername());
            user = userRepository.save(user);
            return user;
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/user")
    public void deleteUser(@RequestParam Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            deleteUser(optionalUser.get().getId());
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
