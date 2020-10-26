package org.netcracker.project.controller;

import org.netcracker.project.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class UserController {

    static User user1 = new User(1, "Jessica", "Andreevna",
            "Moskvina", "dekamussi@yandex.ru", "Jess", "hash");

    @GetMapping("/user")
    public User getUser(@RequestParam Integer id){
        return user1;
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return Collections.singletonList(user1);
    }
}
