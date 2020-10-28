package org.netcracker.project.controller;

import org.netcracker.project.model.User;
import org.netcracker.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
@RestController
public class UserController extends ExceptionHandlerController {

    static List<User> users = new ArrayList<>();

    static User user1 = new User();

    static {
        users.add(user1);
        users.add(new User());
    }

    @Autowired
    UserRepository userRepository;



    @GetMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public User getUser(@RequestParam Integer id){
        System.out.println(userRepository.findByUsername("july"));
        return userRepository.findByUsername("july");
    }

    @PutMapping("/user")
    public User updateUser(@RequestBody User userForUpdate){
        for (User user : users){
            if (user.getId().equals(userForUpdate.getId())){
                user = userForUpdate;
                return user;
            }
        }
        return null;
    }

    @DeleteMapping("/user")
    public void deleteUser(@RequestParam Integer id){
        for (User user : users){
            if (user.equals(users.get(id))){
                users.remove(user);
            }
        }
    }
}
