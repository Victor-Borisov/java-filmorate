package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserStorage userStorage;
    @Autowired
    public UserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        return userStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("User does not exist"));
    }
    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        Integer id = user.getId();
        if (id == null) {
            throw new ValidationException("Bad id");
        }
        findById(id);
        return userStorage.update(user);
    }

    public User addFriend(Integer id, Integer friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        return user;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User user = findById(id);
        User friend = findById(friendId);
        user.deleteFriend(friendId);
        friend.deleteFriend(id);
        return user;
    }

    public List<User> getFriends(Integer id) {
        User user = findById(id);
        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        User user = findById(id);
        User friendUser = findById(friendId);
        return user.getFriends().stream()
                .filter(friendUser.getFriends()::contains)
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
