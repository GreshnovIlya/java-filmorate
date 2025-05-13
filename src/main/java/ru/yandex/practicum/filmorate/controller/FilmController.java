package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    public FilmController(@Autowired @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> findAllFilm() {
        return filmStorage.findAllFilm();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) {
        return filmStorage.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film, Errors errors) {
        findError(errors);
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm, Errors errors) {
        findError(errors);
        return filmStorage.updateFilm(updateFilm);
    }

    @DeleteMapping("/{idFilm}")
    public boolean deleteFilm(@PathVariable int idFilm) {
        return filmStorage.deleteFilm(idFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean likeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmStorage.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmStorage.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void findError(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors().getFirst().getDefaultMessage());
        }
    }
}