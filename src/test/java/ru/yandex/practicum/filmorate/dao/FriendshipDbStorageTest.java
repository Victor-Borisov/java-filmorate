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
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.frienship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FriendshipDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FriendshipDbStorage friendshipDbStorage;
    @Order(210)
    @Test
    void createAndFindFriendshipByUserIdsTest() {
        if (userDbStorage.findAll().size() == 0) {
            User user1 = User.builder().email("mail@yandex.ru").login("dolore").name("Nick Name")
                    .birthday(LocalDate.of(1976, 9, 20)).build();
            userDbStorage.create(user1);
            User user2 = User.builder().email("friend@mail.ru").login("friend").name("friend adipisicing")
                    .birthday(LocalDate.of(1976, 8, 20)).build();
            userDbStorage.create(user2);
            User user3 = User.builder().email("friend@common.ru").login("common").name("common")
                    .birthday(LocalDate.of(2000, 8, 20)).build();
            userDbStorage.create(user3);
        }
        Friendship friendship = Friendship.builder().userId1(1).userId2(2).statusId(1).build();
        friendshipDbStorage.create(friendship);

        Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(1, 2)).isPresent();
        Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(2, 1)).isEmpty();

    }
    @Order(220)
    @Test
    void getFriendIdsByUserIdTest() {
        Assertions.assertThat(friendshipDbStorage.getFriendIdsByUserId(1)).hasSize(1);
        Assertions.assertThat(friendshipDbStorage.getFriendIdsByUserId(2)).hasSize(0);

    }
    @Order(230)
    @Test
    void updateFriendshipTest() {
        Friendship friendship = Friendship.builder().userId1(3).userId2(2).statusId(1).build();
        friendshipDbStorage.create(friendship);
        friendship = Friendship.builder().id(2).userId1(2).userId2(3).statusId(1).build();
        friendshipDbStorage.update(friendship);
        Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(2, 3)).isPresent();
    }

    @Order(240)
    @Test
    void getCommonFriendIdsByUserIdsTest() {
        Friendship friendship = Friendship.builder().id(1).userId1(1).userId2(3).statusId(1).build();
        friendshipDbStorage.update(friendship);
        Assertions.assertThat(friendshipDbStorage.getCommonFriendIdsByUserIds(1, 2)).hasSize(1);
        Assertions.assertThat(friendshipDbStorage.getCommonFriendIdsByUserIds(2, 1)).hasSize(1);
    }
    @Order(250)
    @Test
    void deleteTest() {
        friendshipDbStorage.delete(1, 2);
        Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(1, 2)).isEmpty();
    }


}
