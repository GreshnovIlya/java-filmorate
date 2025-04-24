package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilm() {
        log.info("Получен HTTP запрос на просмотр всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(int id) {
        log.info("Получен HTTP запрос на получение фильма с id = {}", id);
        notFoundFilm(id);
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Получен HTTP запрос на добавление фильма {}", film.getName());
        releaseDateBefore1895(film.getReleaseDate());
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film updateFilm) {
        if (String.valueOf(updateFilm.getId()).equals("null")) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        notFoundFilm(updateFilm.getId());
        log.info("Получен HTTP запрос на обновление фильма {}", films.get(updateFilm.getId()).getName());
        if (!String.valueOf(updateFilm.getReleaseDate()).isEmpty()) {
            releaseDateBefore1895(updateFilm.getReleaseDate());
        }
        updateFilm.setLikes(new HashSet<>());
        films.replace(updateFilm.getId(),updateFilm);
        log.info("Обновлен фильм {}", films.get(updateFilm.getId()).getName());
        return updateFilm;
    }

    @Override
    public Film deleteFilm(int idFilm) {
        if (!films.containsKey(idFilm)) {
            throw new ValidationException("Нет фильма с id = " + idFilm);
        }
        return films.remove(idFilm);
    }

    private void notFoundFilm(int id) {
        if (!films.containsKey(id)) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    private void releaseDateBefore1895(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Неправильно указана дата релиза фильма");
            throw new ValidationException("Неправильно указана дата релиза");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
