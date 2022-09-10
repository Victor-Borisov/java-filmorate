package ru.yandex.practicum.filmorate.storage.frienship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Friendship> findFriendshipByUserIds(Integer id1, Integer id2) {
        final String qs = "SELECT friendship_id, user_id_1, user_id_2, friendship_status_id " +
            "FROM friendship WHERE user_id_1 = ? and user_id_2 = ?";
        final List<Friendship> friendships = jdbcTemplate
            .query(qs, (rs, rowNum) -> mapRowToFriendship(rs), id1, id2);
        return friendships.size() > 0 ? Optional.of(friendships.get(0)) : Optional.empty();
    }

    @Override
    public Friendship create(Friendship friendship) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id_1", friendship.getUserId1());
        values.put("user_id_2", friendship.getUserId2());
        values.put("friendship_status_id", friendship.getStatusId());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("friendship")
            .usingGeneratedKeyColumns("friendship_id");
        friendship.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return friendship;
    }

    @Override
    public Friendship update(Friendship friendship) {
        final String qs = "UPDATE friendship SET user_id_1 = ?, user_id_2 = ?, friendship_status_id = ? " +
            "WHERE friendship_id = ?";
        jdbcTemplate.update(qs, friendship.getUserId1(), friendship.getUserId2(),
            friendship.getStatusId(), friendship.getId());
        return friendship;
    }

    @Override
    public Optional<Friendship> delete(Integer id1, Integer id2) {
        Optional<Friendship> friendship = findFriendshipByUserIds(id1, id2);
        if (friendship.isPresent()) {
            final String qs = "DELETE FROM friendship WHERE friendship_id = ?";
            jdbcTemplate.update(qs, friendship.get().getId());
        }
        return friendship;
    }

    @Override
    public List<Integer> getFriendIdsByUserId(Integer id) {
        final String qs = "SELECT user_id_2 FROM friendship WHERE user_id_1 = ?";
        return jdbcTemplate.queryForList(qs, Integer.class, id);
    }

    @Override
    public List<Integer> getCommonFriendIdsByUserIds(Integer id1, Integer id2) {
        final String qs = "SELECT a.user_id_2 " +
                "FROM ( " +
                "         SELECT f.user_id_2 " +
                "         FROM friendship f " +
                "         WHERE f.user_id_1 = ? " +
                "     ) a " +
                "INNER JOIN ( " +
                "    SELECT f.user_id_2 " +
                "    FROM friendship f " +
                "    WHERE f.user_id_1 = ? " +
                ") b on a.user_id_2  = b.user_id_2";
        return jdbcTemplate.queryForList(qs, Integer.class, id1, id2);
    }

    private Friendship mapRowToFriendship(ResultSet rs) throws SQLException {
        return Friendship.builder()
                .id(rs.getInt("friendship_id"))
                .userId1(rs.getInt("user_id_1"))
                .userId2(rs.getInt("user_id_2"))
                .statusId(rs.getInt("friendship_status_id"))
                .build();
    }
}
