package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAllGenre();
    Optional<Genre> findGenreById(Integer id);
    List<Genre> findGenresByFilmId(Integer filmId);
    void filmGenreBatchUpdate(Integer filmId, List<Genre> genreList);
    void deleteGenreByFilmId(Integer filmId);
}
