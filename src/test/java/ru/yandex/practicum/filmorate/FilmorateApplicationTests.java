package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.frienship.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

	private final UserDbStorage userDbStorage;
	private final FriendshipDbStorage friendshipDbStorage;
	private final FilmDbStorage filmDbStorage;
	private final MpaDbStorage mpaDbStorage;
	private final GenreStorage genreStorage;
	private final FilmLikeDbStorage filmLikeDbStorage;
	private final UserController userController;

	@Order(10)
	@Test
	void createAndFindAllUserTest() {
		User user1 = User.builder().email("mail@yandex.ru").login("dolore").name("Nick Name")
				.birthday(LocalDate.of(1976, 9, 20)).build();
		userDbStorage.create(user1);
		User user2 = User.builder().email("friend@mail.ru").login("friend").name("friend adipisicing")
				.birthday(LocalDate.of(1976, 8, 20)).build();
		userDbStorage.create(user2);
		User user3 = User.builder().email("friend@common.ru").login("common").name("common")
				.birthday(LocalDate.of(2000, 8, 20)).build();
		userDbStorage.create(user3);

		Assertions.assertThat(userDbStorage.findAll()).hasSize(3);
	}
	@Order(20)
	@Test
	void findByIdUserTest() {
		Assertions.assertThat(userDbStorage.findById(1)).isPresent()
				.hasValueSatisfying(user ->
						Assertions.assertThat(user).hasFieldOrPropertyWithValue("email", "mail@yandex.ru")
				);
	}
	@Order(30)
	@Test
	void updateUserTest() {
		User user1 = User.builder().id(1).email("mail2@yandex.ru").login("doloreUpdate").name("est adipisicing")
				.birthday(LocalDate.of(1976, 9, 20)).build();
		Assertions.assertThat(Optional.of(userDbStorage.update(user1))).isPresent()
				.hasValueSatisfying(user ->
						Assertions.assertThat(user).hasFieldOrPropertyWithValue("login", "doloreUpdate")
				);
	}

	@Order(210)
	@Test
	void createAndFindFriendshipByUserIdsTest() {
		Friendship friendship = Friendship.builder().userId1(1).userId2(2).statusId(1).build();
		friendshipDbStorage.create(friendship);

		Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(1, 2)).isPresent();
		Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(2, 1)).isEmpty();
	}
	@Order(220)
	@Test
	void getFriendIdsByUserIdTest() {
		Assertions.assertThat(friendshipDbStorage.getFriendIdsByUserId(1)).hasSize(1);
		Assertions.assertThat(friendshipDbStorage.getFriendIdsByUserId(2)).hasSize(0);
	}
	@Order(230)
	@Test
	void updateFriendshipTest() {
		Friendship friendship = Friendship.builder().userId1(3).userId2(2).statusId(1).build();
		friendshipDbStorage.create(friendship);
		friendship = Friendship.builder().id(2).userId1(2).userId2(3).statusId(1).build();
		friendshipDbStorage.update(friendship);
		Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(2, 3)).isPresent();
	}

	@Order(240)
	@Test
	void getCommonFriendIdsByUserIdsTest() {
		Friendship friendship = Friendship.builder().id(1).userId1(1).userId2(3).statusId(1).build();
		friendshipDbStorage.update(friendship);
		Assertions.assertThat(friendshipDbStorage.getCommonFriendIdsByUserIds(1, 2)).hasSize(1);
		Assertions.assertThat(friendshipDbStorage.getCommonFriendIdsByUserIds(2, 1)).hasSize(1);
	}
	@Order(250)
	@Test
	void deleteTest() {
		friendshipDbStorage.delete(1, 2);
		Assertions.assertThat(friendshipDbStorage.findFriendshipByUserIds(1, 2)).isEmpty();
	}

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
				.hasValueSatisfying(filmGenre ->
						Assertions.assertThat(filmGenre).hasFieldOrPropertyWithValue("filmId", 2)
				);
	}

	@Order(350)
	@Test
	void findGenresByFilmIdTest() {
		Assertions.assertThat(genreStorage.findGenresByFilmId(2)).hasSize(2);
	}

	@Order(360)
	@Test
	void findAllMpaTest() {
		Assertions.assertThat(mpaDbStorage.findAllMpa()).hasSize(5);
	}

	@Order(370)
	@Test
	void findMpaByIdTest() {
		Assertions.assertThat(mpaDbStorage.findMpaById(3)).isPresent()
				.hasValueSatisfying(mpa ->
						Assertions.assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG-13")
				);
	}

	@Order(380)
	@Test
	void findAllGenreTest() {
		Assertions.assertThat(genreStorage.findAllGenre()).hasSize(6);
	}

	@Order(390)
	@Test
	void findGenreByIdTest() {
		Assertions.assertThat(genreStorage.findGenreById(1)).isPresent()
				.hasValueSatisfying(user ->
						Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "Комедия")
				);
	}

	@Order(400)
	@Test
	void createFilmLikeTest() {
		Assertions.assertThat(filmLikeDbStorage.createFilmLike(1, 2)).isPresent()
				.hasValueSatisfying(filmLike ->
						Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("userId", 1)
				);
		Assertions.assertThat(filmLikeDbStorage.createFilmLike(2, 2)).isPresent()
				.hasValueSatisfying(filmLike ->
						Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("userId", 2)
				);
	}

	@Order(420)
	@Test
	void findFilmLikeByUserIdFilmIdTest() {
		Assertions.assertThat(filmLikeDbStorage.findFilmLikeByUserIdFilmId(1, 2)).isPresent()
				.hasValueSatisfying(filmLike ->
						Assertions.assertThat(filmLike).hasFieldOrPropertyWithValue("userId", 1)
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

	@Order(1000)
	@Test
	void createUserShouldUseLoginWithNoName() {
		User user = User.builder()
				.email("viktor1@yandex.ru")
				.login("v1")
				.birthday(LocalDate.of(1973, 5, 15))
				.build();
		User user1 = userController.create(user);
		assertEquals("v1", user1.getName(), "Login should be written as a name when name is not set");
		user.setName(null);
		user.setLogin("v2");
		user.setEmail("viktor2@yandex.ru");
		User user2 = userController.create(user);
		assertEquals("v2", user2.getName(), "Login should be written as a name when name is null");
		user.setName("");
		user.setLogin("v3");
		user.setEmail("viktor3@yandex.ru");
		User user3 = userController.create(user);
		assertEquals("v3", user3.getName(), "Login should be written as a name when name is an empty string");
	}


}
