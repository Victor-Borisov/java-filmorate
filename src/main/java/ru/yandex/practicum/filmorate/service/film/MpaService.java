package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;

import java.util.List;

@Service
public class MpaService {
    private final FilmStorage filmStorage;
    @Autowired
    public MpaService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }
    public Mpa findMpaById(Integer id) {
        return filmStorage.findMpaById(id).orElseThrow(() -> new ObjectDoesNotExistException("Mpa does not exist"));
    }


}
