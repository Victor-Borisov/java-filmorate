package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank(message = "Name must be not blank")
    private String name;

    @NotBlank(message = "Description must be not blank")
    @Size(max = 200, message = "Description must not be longer 200 characters")
    private String description;

    @ReleaseDate(message = "Release date must not be earlier than 1895-12-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Mpa mpa;

    private List<Genre> genres;

    private int rate;
}
