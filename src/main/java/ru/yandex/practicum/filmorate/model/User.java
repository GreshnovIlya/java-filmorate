package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int id;

    @Email(message = "Неправильно указана почта")
    private String email;

    @NotNull()
    @Pattern(regexp = "\\S+", message = "В логине не должно быть пробелов")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не должна быть пустой")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}
