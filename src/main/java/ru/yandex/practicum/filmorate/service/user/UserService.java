package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.frienship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    @Autowired
    public UserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage,
        @Qualifier("inMemoryFriendshipStorage") FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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

    public Friendship addFriend(Integer id, Integer friendId) {
        User user1 = findById(id);
        User user2 = findById(friendId);
        Optional<Friendship> friendship = friendshipStorage.findFriendshipByUserIds(id, friendId);
        if (friendship.isEmpty()) {
            return friendshipStorage.create(Friendship.builder()
                    .id(0)
                    .user1(user1)
                    .user2(user2)
                    .status(1)
                    .build());
        } else {
            return friendship.get();
        }
    }

    public Friendship deleteFriend(Integer id, Integer friendId) {
        User user1 = findById(id);
        User user2 = findById(friendId);
        Optional<Friendship> friendship = friendshipStorage.findFriendshipByUserIds(id, friendId);
        if (friendship.isPresent()) {
            return friendshipStorage.delete(id, friendId).get();
        } else {
            return friendship.get();
        }
    }

    public List<User> getFriends(Integer id) {
        findById(id);
        return friendshipStorage.getFriendIdsByUserId(id).stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        findById(id);
        findById(friendId);
        return friendshipStorage.getCommonFriendIdsByUserIds(id, friendId).stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
