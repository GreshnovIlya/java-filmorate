package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> findAllUser();

    public User findUserById(int id);

    public User createUser(User user);

    public User updateUser(User updateUser);

    public boolean deleteUser(int idUser);

    public User addFriend(int id, int friendId);

    public User deleteFriend(int id,int friendId);

    public List<User> getFriends(int id);

    public List<User> getCommonFriends(int id,int otherId);
}
