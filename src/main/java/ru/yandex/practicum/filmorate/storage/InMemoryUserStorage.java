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
        if (!users.containsKey(id)) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        log.info("Получен HTTP запрос на создание пользователя {}", user.getName());
        user = setNameAsLogin(user);
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
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
        updateUser.setFriends(users.get(updateUser.getId()).getFriends());
        users.replace(updateUser.getId(),updateUser);
        log.info("Обновлен пользователь {}", updateUser.getName());
        return updateUser;
    }

    @Override
    public User deleteUser(int idUser) {
        if (!users.containsKey(idUser)) {
            log.error("При попытке удаления не нашелся пользователь с id = {}", idUser);
            throw new ValidationException("При попытке удаления не нашелся пользователь с id = " + idUser);
        }
        return users.remove(idUser);
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
