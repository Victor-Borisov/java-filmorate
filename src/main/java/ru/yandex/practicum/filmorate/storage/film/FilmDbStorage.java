package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        final String qs = "SELECT f.film_id," +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "m.mpa_id, " +
                "m.mpa_name " +
                "FROM films f " +
                "INNER JOIN rating_mpa m ON m.mpa_id = f.rating_mpa_id;";
        return jdbcTemplate.query(qs, (rs, rowNum) -> {
            final Integer filmId = rs.getInt("film_id");
            return mapRowToFilm(rs, findGenresByFilmId(filmId));
        });
    }

    @Override
    public Optional<Film> findById(Integer id) {
        final String qs = "SELECT f.film_id," +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "m.mpa_id, " +
                "m.mpa_name " +
                "FROM films f " +
                "LEFT JOIN rating_mpa m ON m.mpa_id = f.rating_mpa_id " +
                "WHERE f.film_id = ?";
        final List<Film> films = jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToFilm(rs, findGenresByFilmId(id)), id);
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
        final List<Genre> genres = film.getGenres();
        if (genres != null) {
            final String qsGenre = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            genres.forEach(g -> {
                if (findFilmGenreByFilmIdGenreId(justAddedFilmId, g.getId()).isEmpty()) {
                    jdbcTemplate.update(qsGenre, justAddedFilmId, g.getId());
                }
            });
        }
        return findById(justAddedFilmId);
    }

    @Override
    public Optional<Film> update(Film film) {
        String qs = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(qs, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        qs = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(qs, film.getId());
        final List<Genre> genres = film.getGenres();
        if (genres != null) {
            final String qsGenre = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            genres.forEach(g -> {
                if (findFilmGenreByFilmIdGenreId(film.getId(), g.getId()).isEmpty()) {
                    jdbcTemplate.update(qsGenre, film.getId(), g.getId());
                }
            });
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
            return mapRowToFilm(rs, findGenresByFilmId(filmId));
        }, count);
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
    //FilmGenreDbStorage
    @Override
    public Optional<FilmGenre> findFilmGenreByFilmIdGenreId(Integer filmId, Integer genreId) {
        final String qs = "SELECT film_genre_id, film_id, genre_id " +
                "FROM film_genre WHERE film_id = ? AND genre_id = ?";
        final List<FilmGenre> filmGenres = jdbcTemplate
                .query(qs, (rs, rowNum) -> mapRowToFilmGenre(rs), filmId, genreId);
        return filmGenres.size() > 0 ? Optional.of(filmGenres.get(0)) : Optional.empty();
    }

    @Override
    public List<Genre> findGenresByFilmId(Integer filmId) {
        final String qs = "SELECT g.genre_id, g.genre_name " +
                "FROM genre g " +
                "INNER JOIN film_genre fg on g.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(qs,
                (rs, rowNum) -> mapRowToGenre(rs),
                filmId);
    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private FilmGenre mapRowToFilmGenre(ResultSet rs) throws SQLException {
        return FilmGenre.builder()
                .id(rs.getInt("film_genre_id"))
                .filmId(rs.getInt("film_id"))
                .genreId(rs.getInt("genre_id"))
                .build();
    }
    @Override
    public List<Mpa> findAllMpa() {
        final String qs = "SELECT mpa_id, mpa_name FROM rating_mpa";
        return jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToMpa(rs));
    }

    @Override
    public Optional<Mpa> findMpaById(Integer id) {
        final String qs = "SELECT mpa_id, mpa_name FROM rating_mpa WHERE mpa_id = ?";
        final List<Mpa> mpas = jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToMpa(rs), id);
        return mpas.size() > 0 ? Optional.of(mpas.get(0)) : Optional.empty();
    }

    private Mpa mapRowToMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }
    @Override
    public List<Genre> findAllGenre() {
        final String qs = "SELECT genre_id, genre_name FROM genre";
        return jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToGenre(rs));
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        final String qs = "SELECT genre_id, genre_name FROM genre WHERE genre_id = ?";
        final List<Genre> genres = jdbcTemplate.query(qs, (rs, rowNum) -> mapRowToGenre(rs), id);
        return genres.size() > 0 ? Optional.of(genres.get(0)) : Optional.empty();
    }

}
