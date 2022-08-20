package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(User user);

    User deleteById(Integer id);

}
