package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.frienship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.frienship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    @Autowired
    private static JdbcTemplate jdbcTemplate;

    private static final UserStorage userStorage = new UserDbStorage(jdbcTemplate);
    private static final FriendshipStorage friendshipStorage = new FriendshipDbStorage(jdbcTemplate);
    private static final UserService userService = new UserService(userStorage, friendshipStorage);

    private static final UserController userController = new UserController(userService);

    @Test
    void createUserShouldUseLoginWithNoName() {
        User user = User.builder()
                .email("viktor@yandex.ru")
                .login("v")
                .birthday(LocalDate.of(1973, 5, 15))
                .build();
        User user1 = userController.create(user);
        assertEquals("v", user1.getName(), "Login should be written as a name when name is not set");
        user.setName(null);
        User user2 = userController.create(user);
        assertEquals("v", user2.getName(), "Login should be written as a name when name is null");
        user.setName("");
        User user3 = userController.create(user);
        assertEquals("v", user3.getName(), "Login should be written as a name when name is an empty string");
    }

}
