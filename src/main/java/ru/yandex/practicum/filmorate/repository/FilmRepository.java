package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.repository.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.repository.mapper.MpaMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmRepository {
    protected final JdbcTemplate jdbc;
    protected final FilmMapper filmMapper;
    protected final MpaMapper mpaMapper;
    protected final GenreMapper genreMapper;

    private static final String FIND_ALL_FILMS = "SELECT * FROM films;";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM films WHERE id = ?;";
    private static final String INSERT_FILM = "INSERT INTO films(name, description, release_date, duration, id_mpa) " +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre(id_film, id_genre) VALUES (?, ?);";
    private static final String DELETE_FILM_GENRE = "DELETE FROM film_genre WHERE id_film = ?;";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, id_mpa = ? WHERE id = ?;";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?;";
    private static final String LIKE_FILM = "INSERT INTO liked_film(id_film, id_user) VALUES (?, ?);";
    private static final String DELETE_LIKE_FILM = "DELETE FROM liked_film WHERE id_film = ? AND id_user = ?;";
    private static final String FIND_TOP_FILM = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
            "f.id_mpa, g.genres FROM (SELECT id_film, COUNT(id_user) as likes FROM liked_film GROUP BY id_film " +
            "ORDER BY likes DESC LIMIT ?) as t INNER JOIN films as f ON f.id = t.id_film LEFT JOIN (SELECT id_film, " +
            "array_agg(id_genre) as genres FROM film_genre GROUP BY id_film) as g ON g.id_film = f.id;";

    public List<Film> findAllFilm() {
        return jdbc.query(FIND_ALL_FILMS, filmMapper);
    }

    public Film findFilmById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_FILM_BY_ID, filmMapper, id))
                    .orElseThrow(() -> new NotFoundException("Фильм не найден с ID: " + id));
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм не найден с ID: " + id);
        }
    }

    public Film createFilm(Film film) {
        checkFilm(film);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
            }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            film.setId(id);
            if (!(film.getGenres() == null)) {
                setGenres(film);
            }
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        return film;
    }

    public Film updateFilm(Film updateFilm) {
        Film oldFilm = findFilmById(updateFilm.getId());
        if (updateFilm.getName() == null || updateFilm.getName().isBlank()) {
            updateFilm.setName(oldFilm.getName());
        }
        if (updateFilm.getDescription() == null || updateFilm.getDescription().isBlank()) {
            updateFilm.setDescription(oldFilm.getDescription());
        }
        if (updateFilm.getReleaseDate() == null) {
            updateFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (updateFilm.getDuration() == 0) {
            updateFilm.setDuration(oldFilm.getDuration());
        }
        if (updateFilm.getMpa().getId() == 0) {
            updateFilm.setMpa(oldFilm.getMpa());
        }
        if (updateFilm.getGenres() == null) {
            updateFilm.setGenres(oldFilm.getGenres());
            checkFilm(updateFilm);
        } else {
            checkFilm(updateFilm);
            jdbc.update(DELETE_FILM_GENRE, updateFilm.getId());
            setGenres(updateFilm);
        }

        int rowsUpdated = jdbc.update(UPDATE_FILM,
                updateFilm.getName(), updateFilm.getDescription(), updateFilm.getReleaseDate(),
                updateFilm.getDuration(), updateFilm.getMpa().getId(), updateFilm.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return updateFilm;
    }

    private void setGenres(Film film) {
        for (Genre i : film.getGenres()) {
            GeneratedKeyHolder keyHolderGenre = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_FILM_GENRE, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, film.getId());
                ps.setObject(2, i.getId());
                return ps;
                }, keyHolderGenre);

            Integer idGenre = keyHolderGenre.getKeyAs(Integer.class);

            if (idGenre == null) {
                throw new InternalServerException("Не удалось сохранить данные");
            }
        }
    }

    public boolean deleteFilm(int idFilm) {
        int k = jdbc.update(DELETE_FILM_GENRE, idFilm);
        int d = jdbc.update(DELETE_FILM, idFilm);
        return (d > 0 && k > 0);
    }

    public boolean likeFilm(int idFilm, int userId) {
        deleteLikeFilm(idFilm, userId);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(LIKE_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, idFilm);
            ps.setObject(2, userId);
            return ps;
            }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return true;
    }

    public boolean deleteLikeFilm(int idFilm, int userId) {
        int k = jdbc.update(DELETE_LIKE_FILM, idFilm, userId);
        return k > 0;
    }

    public List<Film> getPopularFilms(int count) {
        return jdbc.query(FIND_TOP_FILM, filmMapper, count);
    }

    private void checkFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("При создании фильма название не должно быть пустым");
        }
        if (film.getMpa().getId() == 0) {
            throw new ValidationException("При создании фильма рейтинг должен быть указан");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("При создании фильма неправильно указана дата релиза");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительности фильма не может быть меньше или равно 0");
        }
        try {
            Optional.ofNullable(jdbc.queryForObject("SELECT * FROM mpa WHERE id = ?", mpaMapper,
                    film.getMpa().getId())).orElseThrow(() -> new NotFoundException("Такого id рейтинга не существует"));
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Такого id рейтинга не существует");
        }
        if (!(film.getGenres() == null)) {
            for (Integer id: film.getGenres().stream().map(Genre::getId).toList()) {
                try {
                    Optional.ofNullable(jdbc.queryForObject("SELECT * FROM genres WHERE id = ?;", genreMapper, id))
                            .orElseThrow(() -> new NotFoundException("Жанра с id = " + id + " не существует"));
                } catch (EmptyResultDataAccessException e) {
                    throw new NotFoundException("Жанра с id = " + id + " не существует");
                }
            }
            if (new HashSet<>(film.getGenres()).size() != film.getGenres().size()) {
                film.setGenres(new HashSet<>(film.getGenres()).stream().toList());
            }
        }
    }
}
