package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.mapper.MpaMapper;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaRepository {
    protected final JdbcTemplate jdbc;
    protected final MpaMapper mpaMapper;

    private static final String FIND_ALL_RATINGS = "SELECT * FROM mpa";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM mpa WHERE id = ?";

    @GetMapping
    public List<Mpa> findAllMpa() {
        return jdbc.query(FIND_ALL_RATINGS, mpaMapper);
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        try {
            Optional.ofNullable(jdbc.queryForObject("SELECT * FROM mpa WHERE id = ?", mpaMapper, id))
                    .orElseThrow(() -> new NotFoundException("Такого id рейтинга не существует"));
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Такого id рейтинга не существует");
        }
        return jdbc.queryForObject(FIND_RATING_BY_ID, mpaMapper, id);
    }
}
