package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    private final UserController userController;
    @Order(1000)
    @Test
    void createUserShouldUseLoginWithNoName() {
        User user = User.builder()
                .email("viktor1@yandex.ru")
                .login("v1")
                .birthday(LocalDate.of(1973, 5, 15))
                .build();
        User user1 = userController.create(user);
        assertEquals("v1", user1.getName(), "Login should be written as a name when name is not set");
        user.setName(null);
        user.setLogin("v2");
        user.setEmail("viktor2@yandex.ru");
        User user2 = userController.create(user);
        assertEquals("v2", user2.getName(), "Login should be written as a name when name is null");
        user.setName("");
        user.setLogin("v3");
        user.setEmail("viktor3@yandex.ru");
        User user3 = userController.create(user);
        assertEquals("v3", user3.getName(), "Login should be written as a name when name is an empty string");
    }

}
