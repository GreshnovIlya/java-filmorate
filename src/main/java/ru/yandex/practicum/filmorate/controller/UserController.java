package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;

    public UserController(@Autowired @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> findAllUser() {
        return userStorage.findAllUser();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
        return userStorage.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user, Errors errors) {
        findError(errors);
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser, Errors errors) {
        findError(errors);
        return userStorage.updateUser(updateUser);
    }

    @DeleteMapping("/{idUser}")
    public boolean deleteUser(@PathVariable int idUser) {
        return userStorage.deleteUser(idUser);
    }

    @PutMapping("/{idUser}/friends/{friendId}")
    public User addFriend(@PathVariable int idUser, @PathVariable int friendId) {
        return userStorage.addFriend(idUser, friendId);
    }

    @DeleteMapping("/{idUser}/friends/{friendId}")
    public User deleteFriend(@PathVariable int idUser, @PathVariable int friendId) {
        return userStorage.deleteFriend(idUser, friendId);
    }

    @GetMapping("/{idUser}/friends")
    public List<User> getFriends(@PathVariable int idUser) {
        return userStorage.getFriends(idUser);
    }

    @GetMapping("/{idUser}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int idUser, @PathVariable int otherId) {
        return userStorage.getCommonFriends(idUser, otherId);
    }

    private void findError(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors().getFirst().getDefaultMessage());
        }
    }
}