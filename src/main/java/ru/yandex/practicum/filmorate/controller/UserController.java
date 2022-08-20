package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping(
        value = "/users",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        return userService.getCommonFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }
}
