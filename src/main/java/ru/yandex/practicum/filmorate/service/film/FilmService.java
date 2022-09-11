package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    @Autowired
    public FilmService(FilmStorage filmStorage,
                       FilmLikeStorage filmLikeStorage) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
    }
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Integer id) {
        return filmStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
    }
    public Film create(Film film) {
        return filmStorage.create(film).orElseThrow(() -> new ObjectDoesNotExistException("Film not created"));
    }

    public Film update(Film film) {
        Integer id = film.getId();
        if (id == null) {
            throw new ValidationException("Bad id");
        }
        findById(id);
        return filmStorage.update(film).orElseThrow(() -> new ObjectDoesNotExistException("Film not updated"));
    }

    public FilmLike addLike(Integer filmId, Integer userId) {
        Optional<FilmLike> filmLike = filmLikeStorage.findFilmLikeByUserIdFilmId(userId, filmId);
        return filmLike.orElseGet(() -> filmLikeStorage.createFilmLike(userId, filmId).orElseThrow(() -> new ObjectDoesNotExistException("Like not added")));
    }

    public FilmLike deleteLike(Integer filmId, Integer userId) {
        return filmLikeStorage.deleteFilmLike(userId, filmId).orElseThrow(() -> new ObjectDoesNotExistException("Like does not exist"));
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }
}
