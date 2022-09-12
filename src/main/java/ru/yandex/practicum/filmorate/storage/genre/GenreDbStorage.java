package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void filmGenreBatchUpdate(Integer filmId, List<Genre> genreList) {
        List<Genre> listWithoutDuplicates = genreList.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
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

    @Override
    public void deleteGenreByFilmId(Integer filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
    }

    private Genre mapRowToGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }


}
