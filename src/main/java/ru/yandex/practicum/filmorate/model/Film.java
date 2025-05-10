package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@Data
public class Film {
    private int id;

    @NotBlank(message = "Неправильно указано название фильма")
    @NotNull
    private String name;

    @Size(max = 200, message = "Максимальный размер описания фильма 200 символов")
    private String description;

    @Past(message = "Дата релиза фильма должна быть в прошлом")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Integer> likes;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}