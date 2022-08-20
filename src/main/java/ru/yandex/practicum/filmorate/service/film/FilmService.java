package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        return filmStorage.findById(id);
    }
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Integer id = film.getId();
        if (id == null) {
            throw new ValidationException("Bad id");
        }
        if (id < 1) {
            throw new ObjectDoesNotExistException("Film does not exist");
        }
        if (filmStorage.findById(id) != null) {
            return filmStorage.update(film);
        } else {
            throw new ObjectDoesNotExistException("Film does not exist");
        }
    }

    public Film addLike(Integer id, Integer userId) {
        Film film = filmStorage.findById(id);
        film.addLike(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.findById(id);
        User user = userStorage.findById(userId);
        if (film != null && user != null) {
            film.deleteLike(userId);
        }
        return film;
    }

    public List<Film> getPopular(String countString) {
        int count;
        try {
            count = Integer.parseInt(countString);
        } catch (Exception e) {
            throw new ValidationException("Bad count");
        }
        List<Film> popularity = filmStorage.findAll();
        popularity.sort(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed());
        return popularity.stream().limit(count).collect(Collectors.toList());
    }
}
