package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final FilmLikeDbStorage filmLikeDbStorage;
    private final UserDbStorage userDbStorage;

    @Order(310)
    @Test
    void createAndFindAllFilmTest() {
        Mpa mpa1 = Mpa.builder().id(5).build();
        Film film1 = Film.builder().name("Film").description("New film decription").duration(190)
                .releaseDate(LocalDate.of(1989, 4, 17))
                .mpa(mpa1)
                .build();
        filmDbStorage.create(film1);
        Mpa mpa2 = Mpa.builder().id(3).build();
        List<Genre> genres2 = Arrays.asList(Genre.builder().id(1).build(), Genre.builder().id(2).build());
        Film film2 = Film.builder().name("New film").description("New film about friends").duration(190)
                .releaseDate(LocalDate.of(1999, 4, 30))
                .mpa(mpa2)
                .genres(genres2)
                .build();
        filmDbStorage.create(film2);
        Assertions.assertThat(filmDbStorage.findAll()).hasSize(2);
    }
    @Order(320)
    @Test
    void findByIdFilmTest() {
        Assertions.assertThat(filmDbStorage.findById(1)).isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "Film")
                );
    }
    @Order(330)
    @Test
    void updateFilmTest() {
        Mpa mpa1 = Mpa.builder().id(5).build();
        Film film1 = Film.builder().id(1).name("Film Updated").description("New film update decription").duration(190)
                .releaseDate(LocalDate.of(1989, 4, 17))
                .mpa(mpa1)
                .build();
        Assertions.assertThat(filmDbStorage.update(film1)).isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film).hasFieldOrPropertyWithValue("name", "Film Updated")
                );
    }
    @Order(340)
    @Test
    void findFilmGenreByFilmIdGenreIdTest() {
        Assertions.assertThat(filmDbStorage.findFilmGenreByFilmIdGenreId(2, 2)).isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 2)
                );
    }

    @Order(350)
    @Test
    void findGenresByFilmIdTest() {
        Assertions.assertThat(filmDbStorage.findGenresByFilmId(2)).hasSize(2);
    }

    @Order(360)
    @Test
    void findAllMpaTest() {
        Assertions.assertThat(filmDbStorage.findAllMpa()).hasSize(5);
    }

    @Order(370)
    @Test
    void findMpaByIdTest() {
        Assertions.assertThat(filmDbStorage.findMpaById(3)).isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "PG-13")
                );
    }

    @Order(380)
    @Test
    void findAllGenreTest() {
        Assertions.assertThat(filmDbStorage.findAllGenre()).hasSize(6);
    }

    @Order(390)
    @Test
    void findGenreByIdTest() {
        Assertions.assertThat(filmDbStorage.findGenreById(1)).isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Order(400)
    @Test
    void createFilmLikeTest() {
        if (userDbStorage.findAll().size() == 0) {
            User user1 = User.builder().email("mail@yandex.ru").login("dolore").name("Nick Name")
                    .birthday(LocalDate.of(1976, 9, 20)).build();
            userDbStorage.create(user1);
            User user2 = User.builder().email("friend@mail.ru").login("friend").name("friend adipisicing")
                    .birthday(LocalDate.of(1976, 8, 20)).build();
            userDbStorage.create(user2);
        }

        Assertions.assertThat(filmLikeDbStorage.createFilmLike(1, 2)).isPresent()
                .hasValueSatisfying(filmLike ->
                        Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("id", 1)
                );
        Assertions.assertThat(filmLikeDbStorage.createFilmLike(2, 2)).isPresent()
                .hasValueSatisfying(filmLike ->
                        Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("id", 2)
                );
    }

    @Order(410)
    @Test
    void findFilmLikeByIdTest() {
        Assertions.assertThat(filmLikeDbStorage.findFilmLikeById(1)).isPresent()
                .hasValueSatisfying(filmLike ->
                        Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("userId", 1)
                );
    }

    @Order(420)
    @Test
    void findFilmLikeByUserIdFilmIdTest() {
        Assertions.assertThat(filmLikeDbStorage.findFilmLikeByUserIdFilmId(1, 2)).isPresent()
                .hasValueSatisfying(filmLike ->
                        Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Order(430)
    @Test
    void getPopularTest() {
        Assertions.assertThat(filmDbStorage.getPopular(10)).first()
                .hasFieldOrPropertyWithValue("name", "New film");
    }
    @Order(440)
    @Test
    void deleteUserTest() {
        userDbStorage.deleteById(2);
        Assertions.assertThat(userDbStorage.findById(2)).isEmpty();
    }

}
