package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film likeFilm(int id, int userId) {
        log.info("Получен HTTP запрос на постановку лайка пользователя с id = {} фильму с id = {}", id, userId);
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        return filmStorage.findFilmById(id);
    }

    public Film deleteLikeFilm(int id, int userId) {
        log.info("Получен HTTP запрос на удаление лайка пользователя с id = {} с фильма с id = {}", id, userId);
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        return filmStorage.findFilmById(id);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получен HTTP запрос на получение {} самых популярных фильмов по лайкам", count);

        return new ArrayList<>();
    }
}
