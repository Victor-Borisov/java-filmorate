package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id;
    private int getId() {
        return ++id;
    }


    @GetMapping
    public List<Film> findAll() {
        log.debug("Current count of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        int id = getId();
        film.setId(id);
        films.put(id, film);
        log.info("Saved film: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        Integer id = film.getId();
        if (id == null || id < 1) {
            throw new ValidationException("Bad id");
        }
        boolean filmExists = films.containsKey(id);
        if (filmExists) {
            films.put(id, film);
            log.info("Updated film: {}", film);
        } else {
            throw new ValidationException("User does not exist");
        }
        return film;
    }
}
