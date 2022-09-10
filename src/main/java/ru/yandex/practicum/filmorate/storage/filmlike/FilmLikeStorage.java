package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.FilmLike;

import java.util.Optional;

public interface FilmLikeStorage {

    Optional<FilmLike> findFilmLikeByUserIdFilmId(Integer userId, Integer filmId);

    Optional<FilmLike> findFilmLikeById(Integer id);

    Optional<FilmLike> createFilmLike(Integer userId, Integer filmId);

    Optional<FilmLike> deleteFilmLike(Integer userId, Integer filmId);

}
