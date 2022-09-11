package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

}
