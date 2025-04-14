package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {
    int id;

    @NotBlank(message = "Неправильно указано название фильма")
    @NotNull
    String name;

    @Size(max = 200, message = "Максимальный размер описания фильма 200 символов")
    String description;

    @Past(message = "Дата релиза фильма должна быть в прошлом")
    LocalDate releaseDate;

    @Positive
    int duration;
}