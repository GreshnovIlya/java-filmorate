package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    public FilmController(@Autowired FilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAllFilm() {
        return inMemoryFilmStorage.findAllFilm();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film, Errors errors) {
        findError(errors);
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updateFilm, Errors errors) {
        findError(errors);
        return inMemoryFilmStorage.updateFilm(updateFilm);
    }

    @DeleteMapping("/{idFilm}")
    public Film deleteFilm(@PathVariable int idFilm) {
        return inMemoryFilmStorage.deleteFilm(idFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    private void findError(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors().getFirst().getDefaultMessage());
        }
    }
}