package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    @Order(10)
    @Test
    void createAndFindAllUserTest() {
        User user1 = User.builder().email("mail@yandex.ru").login("dolore").name("Nick Name")
            .birthday(LocalDate.of(1976, 9, 20)).build();
        userDbStorage.create(user1);
        User user2 = User.builder().email("friend@mail.ru").login("friend").name("friend adipisicing")
                .birthday(LocalDate.of(1976, 8, 20)).build();
        userDbStorage.create(user2);
        User user3 = User.builder().email("friend@common.ru").login("common").name("common")
                .birthday(LocalDate.of(2000, 8, 20)).build();
        userDbStorage.create(user3);

        Assertions.assertThat(userDbStorage.findAll()).hasSize(3);
    }
    @Order(20)
    @Test
    void findByIdUserTest() {
        Assertions.assertThat(userDbStorage.findById(1)).isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("email", "mail@yandex.ru")
                );
    }
    @Order(30)
    @Test
    void updateUserTest() {
        User user1 = User.builder().id(1).email("mail2@yandex.ru").login("doloreUpdate").name("est adipisicing")
                .birthday(LocalDate.of(1976, 9, 20)).build();
        Assertions.assertThat(Optional.of(userDbStorage.update(user1))).isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("login", "doloreUpdate")
                );
    }

}
