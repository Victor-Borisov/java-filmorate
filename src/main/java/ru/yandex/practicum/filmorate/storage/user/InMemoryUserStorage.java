package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;
    private int getId() {
        return ++id;
    }

    @Override
    public List<User> findAll() {
        log.debug("Current count of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        int id = getId();
        user.setId(id);
        users.put(id, user);
        log.info("Saved user: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(id, user);
        log.info("Updated user: {}", user);
        return user;
    }

    @Override
    public Optional<User> deleteById(Integer id) {
        return Optional.ofNullable(users.remove(id));
    }
}
