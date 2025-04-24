package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(int id) {
        log.info("Получен HTTP запрос на получения пользователя с id = {}", id);
        notFoundUser(id);
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        log.info("Получен HTTP запрос на добавление пользователя {}", user.getName());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        user.setFriend(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        if (String.valueOf(updateUser.getId()).equals("null")) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        notFoundUser(updateUser.getId());
        log.info("Получен HTTP запрос на обновление пользователя {}", users.get(updateUser.getId()).getName());
        if (updateUser.getName() == null) {
            updateUser.setName(updateUser.getLogin());
        }
        users.replace(updateUser.getId(),updateUser);
        log.info("Обновлен пользователь {}", users.get(updateUser.getId()).getName());
        return updateUser;
    }

    @Override
    public User deleteUser(int idUser) {
        if (!users.containsKey(idUser)) {
            throw new ValidationException("Нет пользователя с id = " + idUser);
        }
        return users.remove(idUser);
    }

    private void notFoundUser(int id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
