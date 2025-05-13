package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage inMemoryUserStorage;

    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addFriend(int id, int friendId) {
        log.info("Получен HTTP запрос на добавление пользователю с id = {} друга с id = {}", id, friendId);
        log.info("Пользователи с id = {} и {} объявлены как друзья", id, friendId);

        return inMemoryUserStorage.findUserById(id);
    }

    public User deleteFriend(int id, int friendId) {
        log.info("Получен HTTP запрос на удаление из друзей пользователю с id = {} друга с id = {}", id, friendId);
        log.info("Пользователи с id = {} и {} больше не друзья", id, friendId);

        return inMemoryUserStorage.findUserById(id);
    }

    public List<User> getFriends(int id) {
        log.info("Получен HTTP запрос на получение друзей пользователю с id = {}", id);

        return new ArrayList<>();
    }

    public List<User> getCommonFriends(int id, int otherId) {
        log.info("Получен HTTP запрос на получение общих друзей пользователей с id = {} и {}", id, otherId);

        return new ArrayList<>();
    }
}
