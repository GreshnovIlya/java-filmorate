package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен HTTP запрос на добавление пользователя {}", user.getName());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updateUser) {
        if (String.valueOf(updateUser.getId()).equals("null")) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(updateUser.getId())) {
            log.info("Получен HTTP запрос на обновление пользователя {}", users.get(updateUser.getId()).getName());
            if (updateUser.getName() == null) {
                updateUser.setName(updateUser.getLogin());
            }
            log.info("Обновлен пользователь {}", users.get(updateUser.getId()).getName());
            return updateUser;
        }
        log.error("Пользователь с id = {} не найден", updateUser.getId());
        throw new ValidationException("Пользователь с id = " + updateUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}