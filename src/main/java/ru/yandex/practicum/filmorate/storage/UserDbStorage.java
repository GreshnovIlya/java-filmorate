package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAllUsers();
    }

    @Override
    public User findUserById(int id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User createUser(User user) {
        if (user.getBirthday() == null) {
            throw new ValidationException("При создании пользователя дата рождения должен быть указан");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("При создании пользователя логин должен быть указан");
        }
        setNameAsLogin(user);
        return userRepository.createUser(user);
    }

    @Override
    public User updateUser(User updateUser) {
        if (String.valueOf(updateUser.getId()).equals("null")) {
            throw new ValidationException("При обновлении пользователя id должен быть указан");
        }
        return userRepository.updateUser(updateUser);
    }

    @Override
    public boolean deleteUser(int id) {
        return userRepository.deleteUser(id);
    }

    public User addFriend(int idUser, int friendId) {
        return userRepository.addFriend(idUser, friendId);
    }

    @Override
    public User deleteFriend(int idUser, int friendId) {
        return userRepository.deleteFriend(idUser, friendId);
    }

    @Override
    public List<User> getFriends(int idUser) {
        return userRepository.getFriends(idUser);
    }

    @Override
    public List<User> getCommonFriends(int idUser, int otherId) {
        return userRepository.getCommonFriends(idUser, otherId);
    }

    private void setNameAsLogin(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
