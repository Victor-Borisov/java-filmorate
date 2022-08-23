package ru.yandex.practicum.filmorate;

import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.io.ObjectInputFilter;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    private static final UserStorage userStorage = new InMemoryUserStorage();
    private static final UserService userService = new UserService(userStorage);

    private static final UserController userController = new UserController(userService);

    @Test
    void createUserShouldUseLoginWithNoName() {
        User user = new User();
        user.setBirthday(LocalDate.of(1973, 5, 15));
        user.setLogin("v");
        user.setEmail("viktor@yandex.ru");
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
