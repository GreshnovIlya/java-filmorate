package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, MpaRepository.class, GenreRepository.class})
class FilmorateApplicationTests {
	@Autowired
	public final UserDbStorage userStorage;
	@Autowired
	public final FilmDbStorage filmStorage;
	@Autowired
	public final MpaRepository mpaRepository;
	@Autowired
	public final GenreRepository genreRepository;



	@Test
	public void testCreateAndFindUserById() {
		User newUser = new User();
		newUser.setEmail("qwer@yandex.ru");
		newUser.setLogin("qwer");
		newUser.setName("Антон");
		newUser.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(newUser);

		Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(newUser.getId()));

		assertThat(userOptional).isPresent().hasValueSatisfying(user ->
				assertThat(user).hasFieldOrPropertyWithValue("id", newUser.getId()));
	}

	@Test
	public void testFindAllUsers() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		User user2 = new User();
		user2.setEmail("2222@yandex.ru");
		user2.setLogin("222");
		user2.setName("Игорь");
		user2.setBirthday(LocalDate.parse("2001-01-01"));
		userStorage.createUser(user2);

		List<User> allUser = userStorage.findAllUser();
		assertThat(allUser).asList().contains(user1);
		assertThat(allUser).asList().contains(user1);
	}

	@Test
	public void testUpdateUser() {
		User newUser = new User();
		newUser.setEmail("qwer@yandex.ru");
		newUser.setLogin("qwer");
		newUser.setName("Антон");
		newUser.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(newUser);

		User updateUser = new User();
		updateUser.setId(newUser.getId());
		updateUser.setEmail("AAAAA@yandex.ru");
		updateUser.setLogin("AAAAA");
		updateUser.setBirthday(LocalDate.parse("2000-01-01"));

		Optional<User> userAfterUpdate = Optional.ofNullable(userStorage.updateUser(updateUser));

		assertThat(userAfterUpdate).isPresent().hasValueSatisfying(user ->
				assertThat(user).hasFieldOrPropertyWithValue("id", updateUser.getId()));
		assertThat(userAfterUpdate).isPresent().hasValueSatisfying(user ->
				assertThat(user).hasFieldOrPropertyWithValue("email", "AAAAA@yandex.ru"));
		assertThat(userAfterUpdate).isPresent().hasValueSatisfying(user ->
				assertThat(user).hasFieldOrPropertyWithValue("login", "AAAAA"));
		assertThat(userAfterUpdate).isPresent().hasValueSatisfying(user ->
				assertThat(user).hasFieldOrPropertyWithValue("name", "Антон"));
	}

	@Test
	public void testDeleteUser() {
		User newUser = new User();
		newUser.setEmail("qwer@yandex.ru");
		newUser.setLogin("qwer");
		newUser.setName("Антон");
		newUser.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(newUser);
		userStorage.deleteUser(newUser.getId());

        Assertions.assertFalse(userStorage.findAllUser().contains(newUser));
	}

	@Test
	public void testAddAndDeleteFriend() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		User user2 = new User();
		user2.setEmail("2222@yandex.ru");
		user2.setLogin("222");
		user2.setName("Игорь");
		user2.setBirthday(LocalDate.parse("2001-01-01"));
		userStorage.createUser(user2);

		userStorage.addFriend(user1.getId(),user2.getId());
		assertThat(userStorage.getFriends(user1.getId())).asList().contains(user2);
		assertThat(userStorage.getFriends(user2.getId())).asList().isEmpty();

		userStorage.addFriend(user2.getId(),user1.getId());
		assertThat(userStorage.getFriends(user2.getId())).asList().contains(user1);

		userStorage.deleteFriend(user1.getId(),user2.getId());
		System.out.println(userStorage.getFriends(user2.getId()));
		assertThat(userStorage.getFriends(user1.getId())).asList().isEmpty();
	}

	@Test
	public void testGetCommonFriends() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		User user2 = new User();
		user2.setEmail("2222@yandex.ru");
		user2.setLogin("222");
		user2.setName("Игорь");
		user2.setBirthday(LocalDate.parse("2001-01-01"));
		userStorage.createUser(user2);

		User user3 = new User();
		user3.setEmail("333@yandex.ru");
		user3.setLogin("33");
		user3.setName("Наташа");
		user3.setBirthday(LocalDate.parse("2012-01-01"));
		userStorage.createUser(user3);

		userStorage.addFriend(user1.getId(),user2.getId());
		userStorage.addFriend(user2.getId(),user1.getId());
		userStorage.addFriend(user1.getId(),user3.getId());
		userStorage.addFriend(user3.getId(),user1.getId());
		assertThat(userStorage.getCommonFriends(user2.getId(), user3.getId())).asList().contains(user1);
	}

	@Test
	public void testCreateAndFindFilmById() {
		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findFilmById(newFilm.getId()));

		assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
				assertThat(film).hasFieldOrPropertyWithValue("id", newFilm.getId()));
	}

	@Test
	public void testFindAllFilms() {
		Film film1 = new Film();
		film1.setName("123");
		film1.setDescription("qwer");
		film1.setDuration(110);
		film1.setReleaseDate(LocalDate.parse("2000-01-01"));
		film1.setMpa(mpaRepository.findMpaById(1));
		film1.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(film1);

		Film film2 = new Film();
		film2.setName("123456");
		film2.setDescription("qwerty");
		film2.setDuration(11);
		film2.setReleaseDate(LocalDate.parse("2020-01-01"));
		film2.setMpa(mpaRepository.findMpaById(4));
		List<Genre> genres = new ArrayList<Genre>();
		genres.add(genreRepository.findGenreById(1));
		film2.setGenres(genres);
		filmStorage.createFilm(film2);

		List<Film> allFilms = filmStorage.findAllFilm();
		assertThat(allFilms).asList().contains(film1);
		assertThat(allFilms).asList().contains(film2);
	}

	@Test
	public void testUpdateFilm() {
		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);

		Film updateFilm = new Film();
		updateFilm.setId(newFilm.getId());
		updateFilm.setName("123");
		updateFilm.setMpa(mpaRepository.findMpaById(3));
		filmStorage.updateFilm(updateFilm);

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findFilmById(updateFilm.getId()));

		assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
				assertThat(film).hasFieldOrPropertyWithValue("id", updateFilm.getId()));
		assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
				assertThat(film).hasFieldOrPropertyWithValue("name", updateFilm.getName()));
		assertThat(filmOptional).isPresent().hasValueSatisfying(film ->
				assertThat(film).hasFieldOrPropertyWithValue("mpa", updateFilm.getMpa()));
	}

	@Test
	public void testDeleteFilm() {
		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);
		filmStorage.deleteFilm(newFilm.getId());

		Assertions.assertFalse(filmStorage.findAllFilm().contains(newFilm));
	}

	@Test
	public void testLikeFilm() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);

		Assertions.assertTrue(filmStorage.likeFilm(newFilm.getId(), user1.getId()));
	}

	@Test
	public void testDeleteLikeFilm() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);
		filmStorage.likeFilm(newFilm.getId(), user1.getId());

		Assertions.assertTrue(filmStorage.deleteLikeFilm(newFilm.getId(), user1.getId()));
	}

	@Test
	public void testGetPopularFilms() {
		User user1 = new User();
		user1.setEmail("qwer@yandex.ru");
		user1.setLogin("qwer");
		user1.setName("Антон");
		user1.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user1);

		User user2 = new User();
		user2.setEmail("qwer@yandex.ru");
		user2.setLogin("qwer");
		user2.setName("Антон");
		user2.setBirthday(LocalDate.parse("2000-01-01"));
		userStorage.createUser(user2);

		Film newFilm = new Film();
		newFilm.setName("123");
		newFilm.setDescription("qwer");
		newFilm.setDuration(110);
		newFilm.setReleaseDate(LocalDate.parse("2000-01-01"));
		newFilm.setMpa(mpaRepository.findMpaById(1));
		newFilm.setGenres(genreRepository.findAllGenre());
		filmStorage.createFilm(newFilm);
		filmStorage.likeFilm(newFilm.getId(), user1.getId());
		filmStorage.likeFilm(newFilm.getId(), user2.getId());

		List<Film> topFilms = new ArrayList<Film>();
		topFilms.add(newFilm);
		Assertions.assertEquals(filmStorage.getPopularFilms(1), topFilms);
	}

	@Test
	public void testGetAllGenre() {
		Assertions.assertEquals(genreRepository.findAllGenre().size(), 6);
	}

	@Test
	public void testGetAllMpa() {
		Assertions.assertEquals(mpaRepository.findAllMpa().size(), 5);
	}
}