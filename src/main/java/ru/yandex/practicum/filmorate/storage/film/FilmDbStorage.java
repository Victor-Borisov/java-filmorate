package ru.yandex.practicum.filmorate.storage.film;

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
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, GenreStorage genreStorage1) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage1;
    }

    @Override
    public List<Film> findAll() {
        final String qs = "SELECT * FROM films f, rating_mpa m WHERE m.mpa_id = f.rating_mpa_id";
        return jdbcTemplate.query(qs, (rs, rowNum) -> {
            final Integer filmId = rs.getInt("film_id");
            return mapRowToFilm(rs, genreStorage.findGenresByFilmId(filmId));
        });
    }

    @Override
    public Optional<Film> findById(Integer id) {
        final String qs = "SELECT * FROM films f, rating_mpa m WHERE m.mpa_id = f.rating_mpa_id AND f.film_id = ?";
        final List<Film> films = jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToFilm(rs, genreStorage.findGenresByFilmId(id)), id);
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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Integer justAddedFilmId = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        if (film.getGenres() != null) {
            filmGenreBatchUpdate(justAddedFilmId, film.getGenres());
        }
        return findById(justAddedFilmId);
    }

    @Override
    public Optional<Film> update(Film film) {
        String qs = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(qs, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null) {
            filmGenreBatchUpdate(film.getId(), film.getGenres());
        }
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
                "m.mpa_name, " +
                "count(fl.film_id) as like_count " +
                "FROM films f " +
                "LEFT JOIN rating_mpa m ON m.mpa_id = f.rating_mpa_id " +
                "LEFT JOIN film_like fl ON fl.film_id = f.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name " +
                "ORDER BY count(fl.film_id) desc " +
                "LIMIT ?";
        return jdbcTemplate.query(qs, (rs, rowNum) -> {
            final Integer filmId = rs.getInt("film_id");
            return mapRowToFilm(rs, genreStorage.findGenresByFilmId(filmId));
        }, count);
    }

    @Override
    public Optional<FilmGenre> findFilmGenreByFilmIdGenreId(Integer filmId, Integer genreId) {
        final String qs = "SELECT film_id, genre_id FROM film_genre WHERE film_id = ? AND genre_id = ?";
        final List<FilmGenre> filmGenres = jdbcTemplate
                .query(qs, (rs, rowNum) -> mapRowToFilmGenre(rs), filmId, genreId);
        return filmGenres.size() > 0 ? Optional.of(filmGenres.get(0)) : Optional.empty();
    }

    private Film mapRowToFilm(ResultSet rs, List<Genre> genres) throws SQLException {
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
                .genres(genres)
                .build();
    }

    private int [] filmGenreBatchUpdate(Integer filmId, List<Genre> genreList) {
        List<Genre> listWithoutDuplicates = genreList.stream().distinct().collect(Collectors.toList());
        return jdbcTemplate.batchUpdate(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, listWithoutDuplicates.get(i).getId());
                    }
                    public int getBatchSize() {
                        return listWithoutDuplicates.size();
                    }
                });
    }

    private FilmGenre mapRowToFilmGenre(ResultSet rs) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getInt("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
    }


}
