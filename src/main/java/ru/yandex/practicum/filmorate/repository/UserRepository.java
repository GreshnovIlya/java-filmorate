package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.repository.mapper.UserMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepository {
    protected final JdbcTemplate jdbc;
    protected final UserMapper userMapper;
    protected final FriendshipMapper friendshipMapper;

    private static final String FIND_ALL_USERS = "SELECT * FROM users;";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?;";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?);";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?;";
    private static final String DELETED_USER = "DELETE FROM users WHERE id = ?;";
    private static final String ADD_FRIEND_REQUEST = "INSERT INTO user_friendship(id_user, id_friend, friendship) " +
            "VALUES (?, ?, FALSE);";
    private static final String UPDATE_FRIENDSHIP = "UPDATE user_friendship SET friendship = ?, id_user = ?, " +
            "id_friend = ? WHERE id = (SELECT id FROM user_friendship WHERE id_user = ? AND id_friend = ?);";
    private static final String DELETE_FRIEND_BEFORE_ANSWER = "DELETE FROM user_friendship WHERE id = " +
            "(SELECT id FROM user_friendship WHERE id_user = ? AND id_friend = ?);";
    private static final String CHECK_FRIENDSHIP = "SELECT * FROM user_friendship WHERE id_user = ? AND id_friend = ?;";
    private static final String GET_FRIENDS = "SELECT * FROM users WHERE id in (SELECT id_friend FROM " +
            "user_friendship WHERE id_user = ?) UNION SELECT * FROM users WHERE id in " +
            "(SELECT id_user FROM user_friendship WHERE id_friend = ? AND friendship = TRUE)";
    private static final String GET_Common_FRIENDS = "SELECT * FROM users WHERE id in (SELECT t.id_friend FROM " +
            "(SELECT id_friend  FROM user_friendship WHERE id_user = ? UNION SELECT id_user " +
            "FROM user_friendship WHERE id_friend  = ? AND friendship = TRUE) as t INNER JOIN (SELECT id_friend  " +
            "FROM user_friendship WHERE id_user = ? UNION SELECT id_user FROM user_friendship " +
            "WHERE id_friend  = ? AND friendship = TRUE) as k ON t.id_friend = k.id_friend);";

    public List<User> findAllUsers() {
        return jdbc.query(FIND_ALL_USERS, userMapper);
    }

    public User findUserById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_USER_BY_ID, userMapper, id))
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Пользователь не найден с ID: " + id);
        }
    }

    public User createUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, user.getEmail());
            ps.setObject(2, user.getLogin());
            ps.setObject(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
            }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            user.setId(id);
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        return user;
    }

    public User updateUser(User user) {
        User oldUser = findUserById(user.getId());
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(oldUser.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            user.setLogin(oldUser.getLogin());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            System.out.println(oldUser.getName());
            user.setName(oldUser.getName());
        }
        if (user.getBirthday() == null) {
            user.setBirthday(oldUser.getBirthday());
        }
        int rowsUpdated = jdbc.update(UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return user;
    }

    public boolean deleteUser(int id) {
        int d = jdbc.update(DELETED_USER, id);
        return d > 0;
    }

    public User addFriend(int idUser, int friendId) {
        if (idUser == friendId) {
            throw new ValidationException("Нельзя подружится с собой");
        }
        findUserById(idUser);
        findUserById(friendId);
        if (!jdbc.query(CHECK_FRIENDSHIP, friendshipMapper, idUser, friendId).isEmpty() ||
                !jdbc.query(CHECK_FRIENDSHIP, friendshipMapper, friendId, idUser).isEmpty()) {
            try {
                if (idUser != Objects.requireNonNull(jdbc.queryForObject(CHECK_FRIENDSHIP, friendshipMapper, friendId,
                        idUser)).getFirstConnection()) {
                    int rowsUpdated = jdbc.update(UPDATE_FRIENDSHIP, true, friendId, idUser, friendId, idUser);
                    if (rowsUpdated == 0) {
                        throw new InternalServerException("Не удалось обновить данные");
                    }
                }
            } catch (EmptyResultDataAccessException ignored) {

            }
        } else {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(ADD_FRIEND_REQUEST, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, idUser);
                ps.setObject(2, friendId);
                return ps;
                }, keyHolder);

            Integer idFriendship = keyHolder.getKeyAs(Integer.class);

            if (idFriendship == null) {
                throw new InternalServerException("Не удалось сохранить данные");
            }
        }
        return findUserById(idUser);
    }

    public User deleteFriend(int idUser, int friendId) {
        findUserById(idUser);
        findUserById(friendId);
        try {
            if (!Objects.requireNonNull(jdbc.queryForObject(CHECK_FRIENDSHIP, friendshipMapper, idUser,
                    friendId)).isFriendship()) {
                int rowsUpdated = jdbc.update(DELETE_FRIEND_BEFORE_ANSWER, idUser, friendId);
                if (rowsUpdated == 0) {
                    throw new InternalServerException("Не удалось обновить данные");
                }
            }
        } catch (EmptyResultDataAccessException ignored) {

        }
        try {
            if (Objects.requireNonNull(jdbc.queryForObject(CHECK_FRIENDSHIP, friendshipMapper, idUser,
                    friendId)).isFriendship()) {
                int rowsUpdated = jdbc.update(UPDATE_FRIENDSHIP, false, friendId, idUser, idUser, friendId);
                if (rowsUpdated == 0) {
                    throw new InternalServerException("Не удалось обновить данные");
                }
            }
        } catch (EmptyResultDataAccessException ignored) {

        }
        try {
            if (Objects.requireNonNull(jdbc.queryForObject(CHECK_FRIENDSHIP, friendshipMapper, friendId,
                    idUser)).isFriendship()) {
                int rowsUpdated = jdbc.update(UPDATE_FRIENDSHIP, false, friendId, idUser, friendId, idUser);
                if (rowsUpdated == 0) {
                    throw new InternalServerException("Не удалось обновить данные");
                }
            }
        } catch (EmptyResultDataAccessException ignored) {

        }
        return findUserById(idUser);
    }

    public List<User> getFriends(int idUser) {
        findUserById(idUser);
        return jdbc.query(GET_FRIENDS, userMapper, idUser, idUser);
    }

    public List<User> getCommonFriends(int idUser, int otherId) {
        return jdbc.query(GET_Common_FRIENDS, userMapper, idUser, idUser, otherId, otherId);
    }
}
