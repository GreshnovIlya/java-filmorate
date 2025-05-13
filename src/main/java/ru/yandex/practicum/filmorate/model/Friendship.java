package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Friendship {
    private int id;
    private int firstConnection;
    private int secondConnection;
    private boolean friendship;
}
