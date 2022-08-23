package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(Integer id);

    User create(User user);

    User update(User user);

    Optional<User> deleteById(Integer id);

}
