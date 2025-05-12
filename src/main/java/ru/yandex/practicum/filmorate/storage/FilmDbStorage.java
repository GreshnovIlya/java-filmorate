package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final FilmRepository filmRepository;

    @Override
    public List<Film> findAllFilm() {
        return filmRepository.findAllFilm();
    }

    @Override
    public Film findFilmById(int id) {
        return filmRepository.findFilmById(id);
    }

    @Override
    public Film createFilm(Film film) {
        return filmRepository.createFilm(film);
    }

    @Override
    public Film updateFilm(Film updateFilm) {
        return filmRepository.updateFilm(updateFilm);
    }

    @Override
    public boolean deleteFilm(int idFilm) {
        return filmRepository.deleteFilm(idFilm);
    }

    @Override
    public boolean likeFilm(int id, int userId) {
        return filmRepository.likeFilm(id, userId);
    }

    @Override
    public boolean deleteLikeFilm(int id, int userId) {
        return filmRepository.deleteLikeFilm(id, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmRepository.getPopularFilms(count);
    }
}
