package ru.yandex.practicum.filmorate.storage.frienship;

import org.springframework.data.relational.core.sql.In;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipStorage {
    Optional<Friendship> findFriendshipByUserIds(Integer id1, Integer id2);
    Friendship create(Friendship friendship);

    Friendship update(Friendship friendship);

    Optional<Friendship> delete(Integer id1, Integer id2);

    List<Integer> getFriendIdsByUserId(Integer id);

    List<Integer> getCommonFriendIdsByUserIds(Integer id1, Integer id2);

}
