package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Optional<Film> findById(Integer id);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> deleteById(Integer id);

    List<Film> getPopular(int count);
    List<Mpa> findAllMpa();
    Optional<Mpa> findMpaById(Integer id);
    List<Genre> findAllGenre();
    Optional<Genre> findGenreById(Integer id);
    Optional<FilmGenre> findFilmGenreByFilmIdGenreId(Integer filmId, Integer genreId);

    List<Genre> findGenresByFilmId(Integer filmId);

}
