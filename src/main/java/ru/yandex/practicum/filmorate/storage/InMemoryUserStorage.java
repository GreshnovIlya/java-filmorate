package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUser() {
        return users.values().stream().toList();
    }

    @Override
    public User findUserById(int id) {
        log.info("Получен HTTP запрос на получения пользователя с id = {}", id);
        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        log.info("Получен HTTP запрос на создание пользователя {}", user.getName());
        if (user.getBirthday() == null) {
            throw new ValidationException("При создании пользователя дата рождения должен быть указан");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("При создании пользователя логин должен быть указан");
        }
        user = setNameAsLogin(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь {}", user.getName());
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        if (String.valueOf(updateUser.getId()).equals("null")) {
            log.error("При обновлении пользователя id должен быть указан");
            throw new ValidationException("При обновлении пользователя id должен быть указан");
        }
        if (!users.containsKey(updateUser.getId())) {
            log.error("При попытке обновления пользователь с id = {} не найден", updateUser.getId());
            throw new NotFoundException("При попытке обновленияПользователь с id = " + updateUser.getId()
                    + " не найден");
        }
        log.info("Получен HTTP запрос на обновление пользователя {}", users.get(updateUser.getId()).getName());
        setNameAsLogin(updateUser);
        users.replace(updateUser.getId(),updateUser);
        log.info("Обновлен пользователь {}", updateUser.getName());
        return updateUser;
    }

    @Override
    public boolean deleteUser(int idUser) {
        if (!users.containsKey(idUser)) {
            log.error("При попытке удаления не нашелся пользователь с id = {}", idUser);
            throw new ValidationException("При попытке удаления не нашелся пользователь с id = " + idUser);
        }
        users.remove(idUser);
        return true;
    }

    @Override
    public User addFriend(int id, int friendId) {
        return null;
    }

    @Override
    public User deleteFriend(int id, int friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(int id) {
        return List.of();
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return List.of();
    }

    private User setNameAsLogin(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return user;
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
