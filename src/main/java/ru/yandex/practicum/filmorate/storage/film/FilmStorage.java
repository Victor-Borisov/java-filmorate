package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Optional<Film> findById(Integer id);

    Film create(Film film);

    Film update(Film film);

    Optional<Film> deleteById(Integer id);
}
