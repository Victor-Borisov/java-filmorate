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
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(
        value = "/films",
        consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    protected int id;
    public int getId() {
        return ++id;
    }

    @GetMapping
    public List<Film> findAll() {
        log.debug("Current count of films: {}", films.size());
        return films;
    }

    @PostMapping
    public Film create(@RequestBody @Valid @NotNull Film film) {
        int id = getId();
        film.setId(id);
        films.add(film);
        log.info("Saved film: {}", film);
        return film;
    }

    @PutMapping
    public Film upadte(@RequestBody @Valid @NotNull Film film) {
        Integer id = film.getId();
        if (id == null || id < 1) {
            throw new ValidationException("Bad id");
        }
        boolean filmExists = false;
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == id) {
                filmExists = true;
                films.set(i, film);
                log.info("Updated film: {}", film);
            }
        }
        if (!filmExists) {
            throw new ValidationException("User does not exist");
        }
        return film;    }
}
