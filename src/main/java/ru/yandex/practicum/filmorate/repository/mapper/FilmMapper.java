package ru.yandex.practicum.filmorate.repository.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbc;
    private final GenreMapper genreMapper;
    private final MpaMapper mpaMapper;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(getMpa(film.getId()));
        film.setGenres(getGenres(film.getId()));

        return film;
    }

    private Mpa getMpa(Integer filmId) {
        String query = "SELECT m.id, m.name FROM mpa as m JOIN films as f ON m.id = f.id_mpa WHERE f.id = ?";
        return jdbc.queryForObject(query, mpaMapper, filmId);
    }

     private List<Genre> getGenres(Integer filmId) {
        String query = "SELECT g.id, g.name FROM genres as g JOIN film_genre as f ON g.id= f.id_genre " +
                "WHERE f.id_film = ?";
        return jdbc.query(query, genreMapper, filmId);
    }
}
