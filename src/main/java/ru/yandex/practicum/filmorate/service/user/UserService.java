package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    private void validateName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }
    public User create(User user) {
        validateName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        Integer id = user.getId();
        if (id == null) {
            throw new ValidationException("Bad id");
        }
        if (id < 1) {
            throw new ObjectDoesNotExistException("User does not exist");
        }
        if (userStorage.findById(id) != null) {
            validateName(user);
            return userStorage.update(user);
        } else {
            throw new ObjectDoesNotExistException("User does not exist");
        }
    }

    public User addFriend(Integer id, Integer friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        return user;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(id);
        return user;
    }

    public List<User> getFriends(Integer id) {
        User user = userStorage.findById(id);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        User user = userStorage.findById(id);
        User friendUser = userStorage.findById(friendId);
        return user.getFriends().stream()
                .filter(friendUser.getFriends()::contains)
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
