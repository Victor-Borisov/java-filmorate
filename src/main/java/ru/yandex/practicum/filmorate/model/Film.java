package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotNull(message = "Name must be not null")
    @NotBlank(message = "Name must be not blank")
    private String name;

    @NotNull(message = "Description must be not null")
    @NotBlank(message = "Description must be not blank")
    @Size(max = 200, message = "Description must not be longer 200 characters")
    private String description;

    @ReleaseDate(message = "Release date must not be earlier than 1895-12-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
