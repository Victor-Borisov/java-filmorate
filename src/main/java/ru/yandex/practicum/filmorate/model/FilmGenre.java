package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmGenre {
    private Integer id;
    private Integer filmId;
    private Integer genreId;
}
