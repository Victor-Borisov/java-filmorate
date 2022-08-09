package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;
    private int getId() {
        return ++id;
    }
    private void validateName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @GetMapping
    public List<User> findAll() {
        log.debug("Current count of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        int id = getId();
        user.setId(id);
        validateName(user);
        users.put(id, user);
        log.info("Saved user: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        Integer id = user.getId();
        if (id == null || id < 1) {
            throw new ValidationException("Bad id");
        }
        boolean userExists = users.containsKey(id);
        if (userExists) {
            validateName(user);
            users.put(id, user);
            log.info("Updated user: {}", user);
        } else {
            throw new ValidationException("User does not exist");
        }
        return user;
    }
}
