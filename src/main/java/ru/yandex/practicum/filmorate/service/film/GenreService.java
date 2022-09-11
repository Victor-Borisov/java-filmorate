package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;
    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }
    public List<Genre> findAllGenre() {
        return genreStorage.findAllGenre();
    }
    public Genre findGenreById(Integer id) {
        return genreStorage.findGenreById(id).orElseThrow(() -> new ObjectDoesNotExistException("Genre does not exist"));
    }

}
