package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private Integer id;
    private int userId1;
    private int userId2;
    private int statusId;
}
