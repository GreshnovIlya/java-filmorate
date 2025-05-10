package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    public List<User> findAllUser();

    public User findUserById(int id);

    public User createUser(User user);

    public User updateUser(User updateUser);

    public User deleteUser(int idUser);
}
