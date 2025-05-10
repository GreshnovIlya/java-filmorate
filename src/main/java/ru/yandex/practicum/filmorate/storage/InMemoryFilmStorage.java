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
        if (!films.containsKey(id)) {
            log.error("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Получен HTTP запрос на добавление фильма {}", film.getName());
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("При добавлении фильма неправильно указана дата релиза");
            throw new ValidationException("При добавлении фильма неправильно указана дата релиза");
        }
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film updateFilm) {
        if (String.valueOf(updateFilm.getId()).equals("null")) {
            log.error("При обновлении фильма id должен быть указан");
            throw new ValidationException("При обновлении фильма id должен быть указан");
        }
        if (!films.containsKey(updateFilm.getId())) {
            log.error("При обновлении фильм с id = {} не найден", updateFilm.getId());
            throw new NotFoundException("При обновлении фильм с id = " + updateFilm.getId() + " не найден");
        }
        log.info("Получен HTTP запрос на обновление фильма {}", films.get(updateFilm.getId()).getName());
        if (!String.valueOf(updateFilm.getReleaseDate()).isEmpty()) {
            if (updateFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                log.error("При обновлении фильма неправильно указана дата релиза");
                throw new ValidationException("При обновлении фильма неправильно указана дата релиза");
            }
        }
        updateFilm.setLikes(films.get(updateFilm.getId()).getLikes());
        films.replace(updateFilm.getId(),updateFilm);
        log.info("Обновлен фильм {}", films.get(updateFilm.getId()).getName());
        return updateFilm;
    }

    @Override
    public Film deleteFilm(int idFilm) {
        if (!films.containsKey(idFilm)) {
            throw new ValidationException("При попытке удаления не найден фильм с id = " + idFilm);
        }
        return films.remove(idFilm);
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
