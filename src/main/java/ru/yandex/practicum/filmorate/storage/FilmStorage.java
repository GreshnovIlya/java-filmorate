package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    public List<Film> findAllFilm();

    public Film findFilmById(int id);

    public Film createFilm(Film film);

    public Film updateFilm(Film updateFilm);

    public boolean deleteFilm(int idFilm);

    public boolean likeFilm(int id, int userId);

    public boolean deleteLikeFilm(int id, int userId);

    public List<Film> getPopularFilms(int count);
}
