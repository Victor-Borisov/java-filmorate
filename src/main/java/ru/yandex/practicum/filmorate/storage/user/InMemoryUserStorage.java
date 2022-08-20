package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
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

    @Override
    public List<User> findAll() {
        log.debug("Current count of users: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long id) {
        return null;
    }

    @Override
    public User create(User user) {
        int id = getId();
        user.setId(id);
        validateName(user);
        users.put(id, user);
        log.info("Saved user: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
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

    @Override
    public User deleteById(long id) {
        return null;
    }
}
