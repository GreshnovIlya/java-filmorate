package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage inMemoryUserStorage;
    private final UserService userService;

    public UserController(@Autowired UserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAllUser() {
        return inMemoryUserStorage.findAllUser();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
        return inMemoryUserStorage.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user, Errors errors) {
        findError(errors);
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updateUser, Errors errors) {
        findError(errors);
        return inMemoryUserStorage.updateUser(updateUser);
    }

    @DeleteMapping("/{idUser}")
    public User deleteUser(@PathVariable int idUser) {
        return inMemoryUserStorage.deleteUser(idUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    private void findError(Errors errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors.getAllErrors().getFirst().getDefaultMessage());
        }
    }
}