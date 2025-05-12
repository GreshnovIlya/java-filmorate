package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Data
public class Film {
    private int id;

    private String name;

    @Size(max = 200, message = "Максимальный размер описания фильма 200 символов")
    private String description;

    @Past(message = "Дата релиза фильма должна быть в прошлом")
    private LocalDate releaseDate;

    private int duration;

    private Mpa mpa;

    private List<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa,
                List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }
}