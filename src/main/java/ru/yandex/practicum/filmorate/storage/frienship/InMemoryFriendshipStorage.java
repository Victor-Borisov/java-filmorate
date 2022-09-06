package ru.yandex.practicum.filmorate.storage.frienship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.*;

@Component
@Slf4j
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Integer, Friendship> friendships = new HashMap<>();
    private int id;
    private int getId() {
        return ++id;
    }

    @Override
    public Optional<Friendship> findFriendshipByUserIds(Integer id1, Integer id2) {
        Optional<Friendship> friendshipDelete = Optional.empty();
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUser1().getId() == id1 && friendship.getUser2().getId() == id2) {
                friendshipDelete = Optional.of(friendship);
                break;
            }
            if (friendship.getUser1().getId() == id2 && friendship.getUser2().getId() == id1) {
                friendshipDelete = Optional.of(friendship);
                break;
            }
        }
        return friendshipDelete;
    }

    @Override
    public Friendship create(Friendship friendship) {
        int id = getId();
        friendship.setId(id);
        friendships.put(id, friendship);
        log.info("Saved friendship: {}", friendship);
        return friendship;
    }

    @Override
    public Friendship update(Friendship friendship) {
        friendships.put(id, friendship);
        log.info("Updated user: {}", friendship);
        return friendship;
    }

    @Override
    public Optional<Friendship> delete(Integer id1, Integer id2) {
        Optional<Friendship> friendshipDelete = findFriendshipByUserIds(id1, id2);
        if (friendshipDelete.isPresent()) {
            friendships.remove(friendshipDelete.get().getId());
        }
        return friendshipDelete;
    }

    /*Get list of user ids, who is friend user with id in parameter */
    @Override
    public List<Integer> getFriendIdsByUserId(Integer id) {
        List<Integer> ids = new ArrayList<>();
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUser1().getId() == id) {
                ids.add(friendship.getUser2().getId());
            }
            if (friendship.getUser2().getId() == id) {
                ids.add(friendship.getUser1().getId());
            }
        }
        return ids;
    }

    @Override
    public List<Integer> getCommonFriendIdsByUserIds(Integer id1, Integer id2) {
        List<Integer> ids = new ArrayList<>();
        Integer id3;
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUser1().getId() == id1 && friendship.getUser2().getId() != id2) {
                id3 = friendship.getUser2().getId();
                for (Friendship interFriendship : friendships.values()) {
                    if (interFriendship.getUser1().getId() == id3 && interFriendship.getUser2().getId() == id2) {
                        ids.add(interFriendship.getUser1().getId());
                    } else if (interFriendship.getUser2().getId() == id3 && interFriendship.getUser1().getId() == id2) {
                        ids.add(interFriendship.getUser2().getId());
                    }
                }
            }
        }
        return ids;
    }
}
