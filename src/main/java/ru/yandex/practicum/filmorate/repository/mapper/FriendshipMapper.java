package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getInt("id"));
        friendship.setFirstConnection(rs.getInt("id_user"));
        friendship.setSecondConnection(rs.getInt("id_friend"));
        friendship.setFriendship(rs.getBoolean("friendship"));

        return friendship;
    }
}