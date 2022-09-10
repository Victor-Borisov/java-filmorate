package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class GenreService {
    private final FilmStorage filmStorage;
    @Autowired
    public GenreService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Genre> findAllGenre() {
        return filmStorage.findAllGenre();
    }
    public Genre findGenreById(Integer id) {
        return filmStorage.findGenreById(id).orElseThrow(() -> new ObjectDoesNotExistException("Genre does not exist"));
    }

}
