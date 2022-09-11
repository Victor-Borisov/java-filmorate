package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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


}
