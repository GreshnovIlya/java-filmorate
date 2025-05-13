package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class User {
    private int id;

    @Email(message = "Неправильно указана почта")
    private String email;

    @Pattern(regexp = "\\S+", message = "В логине не должно быть пробелов")
    private String login;

    private String name;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
