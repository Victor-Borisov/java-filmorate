package ru.yandex.practicum.filmorate.storage.filmlike;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLike;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<FilmLike> findFilmLikeByUserIdFilmId(Integer userId, Integer filmId) {
        final String qs = "SELECT film_like_id, user_id, film_id " +
                "FROM film_like WHERE user_id = ? and film_id = ?";
        final List<FilmLike> filmLike = jdbcTemplate
                .query(qs, (rs, rowNum) -> mapRowToFilmLike(rs), userId, filmId);
        return filmLike.size() > 0 ? Optional.of(filmLike.get(0)) : Optional.empty();
    }

    @Override
    public Optional<FilmLike> findFilmLikeById(Integer id) {
        final String qs = "SELECT film_like_id, user_id, film_id " +
                "FROM film_like WHERE film_like_id = ?";
        final List<FilmLike> filmLike = jdbcTemplate
                .query(qs, (rs, rowNum) -> mapRowToFilmLike(rs), id);
        return filmLike.size() > 0 ? Optional.of(filmLike.get(0)) : Optional.empty();
    }

    @Override
    public Optional<FilmLike> createFilmLike(Integer userId, Integer filmId) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userId);
        values.put("film_id", filmId);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_like")
                .usingGeneratedKeyColumns("film_like_id");
        return findFilmLikeById(simpleJdbcInsert.executeAndReturnKey(values).intValue());
    }

    @Override
    public Optional<FilmLike> deleteFilmLike(Integer userId, Integer filmId) {
        Optional<FilmLike> filmLike = findFilmLikeByUserIdFilmId(userId, filmId);
        if (filmLike.isPresent()) {
            final String qs = "DELETE FROM film_like WHERE film_like_id = ?";
            jdbcTemplate.update(qs, filmLike.get().getId());
        }
        return filmLike;
    }

    private FilmLike mapRowToFilmLike(ResultSet rs) throws SQLException {
        return FilmLike.builder()
                .id(rs.getInt("film_like_id"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .build();
    }


}
