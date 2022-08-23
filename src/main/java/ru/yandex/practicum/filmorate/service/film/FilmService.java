package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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
        findById(id);
        return filmStorage.update(film);
    }

    public Film addLike(Integer id, Integer userId) {
        Film film = findById(id);
        film.addLike(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        Film film = findById(id);
        userService.findById(userId);
        film.deleteLike(userId);
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> popularity = filmStorage.findAll();
        popularity.sort(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed());
        return popularity.stream().limit(count).collect(Collectors.toList());
    }
}
