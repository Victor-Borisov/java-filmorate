package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film findById(long id);

    Film create(Film film);

    Film update(Film film);

    Film deleteById(long id);
}
