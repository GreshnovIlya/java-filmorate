package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        log.info("Получен HTTP запрос на просмотр всех фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен HTTP запрос на добавление фильма {}", film.getName());
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Неправильно указана дата релиза фильма");
            throw new ValidationException("Неправильно указана дата релиза фильма");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updateFilm) {
        if (String.valueOf(updateFilm.getId()).equals("null")) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(updateFilm.getId())) {
            log.info("Получен HTTP запрос на обновление фильма {}", films.get(updateFilm.getId()).getName());
            if (!String.valueOf(updateFilm.getReleaseDate()).isEmpty()) {
                if (updateFilm.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
                    log.error("Неправильно указана дата релиза фильма");
                    throw new ValidationException("Неправильно указана дата релиза");
                }
            }
            log.info("Обновлен фильм {}", films.get(updateFilm.getId()).getName());
            return updateFilm;
        }
        log.error("Фильм с id = {} не найден", updateFilm.getId());
        throw new ValidationException("Фильм с id = " + updateFilm.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}