package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        return filmStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
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
        if (filmStorage.findById(id).isPresent()) {
            return filmStorage.update(film);
        } else {
            throw new ObjectDoesNotExistException("Film does not exist");
        }
    }

    public Film addLike(Integer id, Integer userId) {
        Film film = filmStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
        film.addLike(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
        User user = userStorage.findById(userId).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
        film.deleteLike(userId);
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> popularity = filmStorage.findAll();
        popularity.sort(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed());
        return popularity.stream().limit(count).collect(Collectors.toList());
    }
}
