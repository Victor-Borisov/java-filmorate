package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private Integer id;
    private User user1;
    private User user2;
    private int status;
}
