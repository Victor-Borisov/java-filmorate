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
        Optional<Friendship> friendshipFound = Optional.empty();
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUserId1() == id1 && friendship.getUserId2() == id2) {
                friendshipFound = Optional.of(friendship);
                break;
            }
            if (friendship.getUserId1() == id2 && friendship.getUserId2() == id1) {
                friendshipFound = Optional.of(friendship);
                break;
            }
        }
        return friendshipFound;
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
        friendshipDelete.ifPresent(friendship -> friendships.remove(friendship.getId()));
        return friendshipDelete;
    }

    /*Get list of user ids, who is friend user with id in parameter */
    @Override
    public List<Integer> getFriendIdsByUserId(Integer id) {
        List<Integer> ids = new ArrayList<>();
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUserId1() == id) {
                ids.add(friendship.getUserId2());
            }
            if (friendship.getUserId2() == id) {
                ids.add(friendship.getUserId1());
            }
        }
        return ids;
    }

    @Override
    public List<Integer> getCommonFriendIdsByUserIds(Integer id1, Integer id2) {
        List<Integer> ids = new ArrayList<>();
        int id3;
        for (Friendship friendship : friendships.values()) {
            if (friendship.getUserId1() == id1 && friendship.getUserId2() != id2) {
                id3 = friendship.getUserId2();
                for (Friendship interFriendship : friendships.values()) {
                    if (interFriendship.getUserId1() == id3 && interFriendship.getUserId2() == id2) {
                        ids.add(interFriendship.getUserId1());
                    } else if (interFriendship.getUserId2() == id3 && interFriendship.getUserId1() == id2) {
                        ids.add(interFriendship.getUserId2());
                    }
                }
            }
        }
        return ids;
    }
}
