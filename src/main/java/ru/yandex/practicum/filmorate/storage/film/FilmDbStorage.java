package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        final String qs = "SELECT * FROM films f, rating_mpa m WHERE m.mpa_id = f.rating_mpa_id";
        return jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public Optional<Film> findById(Integer id) {
        final String qs = "SELECT * FROM films f, rating_mpa m WHERE m.mpa_id = f.rating_mpa_id AND f.film_id = ?";
        final List<Film> films = jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToFilm(rs), id);
        return films.size() > 0 ? Optional.of(films.get(0)) : Optional.empty();
    }

    @Override
    public Optional<Film> create(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", Date.valueOf(film.getReleaseDate()));
        values.put("duration", film.getDuration());
        values.put("rating_mpa_id", film.getMpa().getId());
        values.put("rate", 0);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        return findById(simpleJdbcInsert.executeAndReturnKey(values).intValue());
    }

    @Override
    public Optional<Film> update(Film film) {
        String qs = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(qs, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        return findById(film.getId());
    }

    @Override
    public Optional<Film> deleteById(Integer id) {
        Optional<Film> film = findById(id);
        if (film.isPresent()) {
            final String qs = "DELETE FROM films WHERE film_id = ?";
            jdbcTemplate.update(qs, id);
        }
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        final String qs = "SELECT f.film_id," +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "m.mpa_id, " +
                "m.mpa_name " +
                "FROM films f " +
                "LEFT JOIN rating_mpa m ON m.mpa_id = f.rating_mpa_id " +
                "ORDER BY f.rate desc " +
                "LIMIT ?";
        return jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToFilm(rs), count);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Mpa mpa = Mpa.builder().
                id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .build();
    }

    private FilmGenre mapRowToFilmGenre(ResultSet rs) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getInt("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
    }


}
