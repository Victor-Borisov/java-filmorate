package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final GenreStorage genreStorage;

    public List<Film> findAll() {
        List<Film> filmList = filmStorage.findAll();
        filmList.forEach(film -> film.setGenres(genreStorage.findGenresByFilmId(film.getId())));
        return filmList;
    }

    public Film findById(Integer id) {
        Film film = filmStorage.findById(id).orElseThrow(() -> new ObjectDoesNotExistException("Film does not exist"));
        film.setGenres(genreStorage.findGenresByFilmId(film.getId()));
        return film;
    }
    public Film create(Film film) {
        Film createdFilm = filmStorage.create(film).orElseThrow(() -> new ObjectDoesNotExistException("Film not created"));
        if (film.getGenres() != null) {
            genreStorage.filmGenreBatchUpdate(createdFilm.getId(), film.getGenres());
        }
        createdFilm.setGenres(genreStorage.findGenresByFilmId(createdFilm.getId()));
        return createdFilm;
    }

    public Film update(Film film) {
        Integer id = film.getId();
        if (id == null) {
            throw new ValidationException("Bad id");
        }
        findById(id);
            Film updatedFilm = filmStorage.update(film).orElseThrow(() -> new ObjectDoesNotExistException("Film not updated"));
        genreStorage.deleteGenreByFilmId(id);
        if (film.getGenres() != null) {
            genreStorage.filmGenreBatchUpdate(id, film.getGenres());
        }
        updatedFilm.setGenres(genreStorage.findGenresByFilmId(id));
        return updatedFilm;
    }

    public FilmLike addLike(Integer filmId, Integer userId) {
        Optional<FilmLike> filmLike = filmLikeStorage.findFilmLikeByUserIdFilmId(userId, filmId);
        return filmLike.orElseGet(() -> filmLikeStorage.createFilmLike(userId, filmId).orElseThrow(() -> new ObjectDoesNotExistException("Like not added")));
    }

    public FilmLike deleteLike(Integer filmId, Integer userId) {
        return filmLikeStorage.deleteFilmLike(userId, filmId).orElseThrow(() -> new ObjectDoesNotExistException("Like does not exist"));
    }

    public List<Film> getPopular(int count) {
        List<Film> filmList = filmStorage.getPopular(count);
        filmList.forEach(film -> film.setGenres(genreStorage.findGenresByFilmId(film.getId())));
        return filmList;
    }
}
