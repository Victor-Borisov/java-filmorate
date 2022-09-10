package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage, FilmLikeStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    //For fast search
    private final Map<Integer, Integer> filmLikeCount = new HashMap<>();
    private final Map<Integer, FilmLike> filmLikes = new HashMap<>();
    private final Map<Integer, String> mpas = Map.of(
            1, "G",
            2, "PG",
            3, "PG-13",
            4, "R",
            5, "NC-17");
    private final Map<Integer, String> genres = Map.of(
            1, "Комедия",
            2, "Драма",
            3, "Мультфильм",
            4, "Триллер",
            5, "Документальный",
            6, "Боевик");
    private int id = 0;
    private int likeId = 0;
    private int getId() {
        return ++id;
    }
    private int getLikeId() {
        return ++likeId;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Current count of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.of(films.get(id));
    }

    @Override
    public Optional<Film> create(Film film) {
        int id = getId();
        film.setId(id);
        films.put(id, film);
        log.info("Saved film: {}", film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> update(Film film) {
        films.put(id, film);
        log.info("Updated user: {}", film);
        return Optional.of(film);
    }

    @Override
    public Optional<Film> deleteById(Integer id) {
        return Optional.of(films.remove(id));
    }

    @Override
    public List<Film> getPopular(int count) {
        List<Film> popularity = findAll();
        popularity.sort(Comparator.comparingInt((Film film) -> filmLikeCount.get(film.getId())).reversed());
        return popularity.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Mpa> findAllMpa() {
        return mpas.entrySet().stream()
                .map(e -> Mpa.builder().id(e.getKey()).name(e.getValue()).build())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Mpa> findMpaById(Integer id) {
        return mpas.entrySet().stream()
                .filter(e -> e.getKey().equals(id))
                .map(e -> Mpa.builder().id(e.getKey()).name(e.getValue()).build())
                .findFirst();
    }

    @Override
    public List<Genre> findAllGenre() {
        return genres.entrySet().stream()
                .map(e -> Genre.builder().id(e.getKey()).name(e.getValue()).build())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        return genres.entrySet().stream()
                .filter(e -> e.getKey().equals(id))
                .map(e -> Genre.builder().id(e.getKey()).name(e.getValue()).build())
                .findFirst();
    }

    @Override
    public Optional<FilmGenre> findFilmGenreByFilmIdGenreId(Integer filmId, Integer genreId) {
        return Optional.empty();
    }

    @Override
    public List<Genre> findGenresByFilmId(Integer filmId) {
        return null;
    }

    @Override
    public Optional<FilmLike> findFilmLikeByUserIdFilmId(Integer userId, Integer filmId) {
        Optional<FilmLike> filmLikeFound = Optional.empty();
        for (FilmLike filmLike : filmLikes.values()) {
            if (filmLike.getFilmId().equals(filmId) && filmLike.getUserId().equals(userId)) {
                filmLikeFound = Optional.of(filmLike);
                break;
            }
        }
        return filmLikeFound;
    }

    @Override
    public Optional<FilmLike> findFilmLikeById(Integer id) {
        return Optional.of(filmLikes.get(id));
    }

    @Override
    public Optional<FilmLike> createFilmLike(Integer userId, Integer filmId) {
        int id = getLikeId();
        FilmLike filmLike = FilmLike.builder()
                .id(id)
                .userId(userId)
                .filmId(filmId)
                .build();
        filmLikes.put(id, filmLike);
        log.info("Saved Like: {}", filmLike);
        Integer likeCount = filmLikeCount.get(filmId);
        filmLikeCount.put(filmId, ++likeCount);
        return Optional.of(filmLike);

    }

    @Override
    public Optional<FilmLike> deleteFilmLike(Integer userId, Integer filmId) {
        Optional<FilmLike> filmLike = findFilmLikeByUserIdFilmId(userId, filmId);
        if (filmLike.isPresent()) {
            Integer likeCount = filmLikeCount.get(filmId);
            filmLikeCount.put(filmId, --likeCount);
            return Optional.of(filmLikes.remove(filmLike.get().getId()));
        } else {
            return Optional.empty();
        }
    }
}
