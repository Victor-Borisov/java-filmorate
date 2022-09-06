package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        final String sq = "SELECT user_id, email, login, name, birthday FROM users;";
        return jdbcTemplate.query(sq, (rs, rowNum) -> mapRowToUser(rs));
    }

    @Override
    public Optional<User> findById(Integer id) {
        final String sq = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?;";
        final List<User> users = jdbcTemplate.query(sq, (rs, rowNum) -> mapRowToUser(rs), id);
        return users.size() > 0 ? Optional.of(users.get(0)) : Optional.empty();
    }

    @Override
    public User create(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("users")
            .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        final String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return null;
    }

    @Override
    public Optional<User> deleteById(Integer id) {
        Optional<User> user = findById(id);
        final String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
        return user;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return User.builder()
            .id(rs.getInt("user_id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("name"))
            .birthday(rs.getDate("birthday").toLocalDate())
            .build();
    }
}
