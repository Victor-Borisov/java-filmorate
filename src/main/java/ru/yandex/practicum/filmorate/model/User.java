package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Integer id;

    @NotBlank(message = "Email must be not blank")
    @Email(message = "Email is incorrect")
    private String email;

    @NotBlank(message = "Login must be not blank")
    @Pattern(regexp = "^\\S*$", message = "Login must not contain any space")
    private String login;

    private String name;
    @PastOrPresent

    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void deleteFriend(Integer id) {
        friends.remove(id);
    }
}
