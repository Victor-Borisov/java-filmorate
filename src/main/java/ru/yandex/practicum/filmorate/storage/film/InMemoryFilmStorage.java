package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;
    private int getId() {
        return ++id;
    }

    @Override
    public List<Film> findAll() {
        log.debug("Current count of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Integer id) {
        boolean filmExists = films.containsKey(id);
        if (filmExists) {
            return films.get(id);
        } else {
            throw new ObjectDoesNotExistException("Film does not exist");
        }
    }

    @Override
    public Film create(Film film) {
        int id = getId();
        film.setId(id);
        films.put(id, film);
        log.info("Saved film: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(id, film);
        log.info("Updated user: {}", film);
        return film;
    }

    @Override
    public Film deleteById(Integer id) {
        boolean filmExists = films.containsKey(id);
        if (filmExists) {
            return films.remove(id);
        } else {
            return null;
        }
    }
}
