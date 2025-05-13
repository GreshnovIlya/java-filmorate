package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mapper.GenreMapper;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreRepository {
    protected final JdbcTemplate jdbc;
    protected final GenreMapper genreMapper;

    private static final String FIND_ALL_GENRES = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";

    public List<Genre> findAllGenre() {
        return jdbc.query(FIND_ALL_GENRES, genreMapper);
    }

    public Genre findGenreById(@PathVariable int id) {
        try {
            Optional.ofNullable(jdbc.queryForObject("SELECT * FROM genres WHERE id = ?", genreMapper, id))
                    .orElseThrow(() -> new NotFoundException("Жанра с id = " + id + " не существует"));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанра с id = " + id + " не существует");
        }
        return jdbc.queryForObject(FIND_GENRE_BY_ID, genreMapper, id);
    }
}
