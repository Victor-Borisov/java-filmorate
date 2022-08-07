package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    private final List<User> users = new ArrayList<>();
    protected int id;
    public int getId() {
        return ++id;
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Current count of users: {}", users.size());
        return users;
    }

    @PostMapping
    public User create(@RequestBody @Valid @NotNull User user) {
        int id = getId();
        user.setId(id);
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        log.info("Saved user: {}", user);
        return user;
    }

    @PutMapping
    public User upadte(@RequestBody @Valid @NotNull User user) {
        Integer id = user.getId();
        if (id == null || id < 1) {
            throw new ValidationException("Bad id");
        }
        boolean userExists = false;
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                userExists = true;
                users.set(i, user);
                log.info("Updated user: {}", user);
            }
        }
        if (!userExists) {
            throw new ValidationException("User does not exist");
        }
        return user;
    }
}
