package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private Integer id;

    @NotNull(message = "Email must be not null")
    @NotBlank(message = "Email must be not blank")
    @Email(message = "Email is incorrect")
    private String email;

    @NotNull(message = "Login must be not null")
    @NotBlank(message = "Login must be not blank")
    @Pattern(regexp = "^\\S*$", message = "Login must not contain any space")
    private String login;

    private String name;
    @Past
    private LocalDate birthday;
}
