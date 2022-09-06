MERGE INTO friendship_status (status_id, status_name)
KEY(status_id)
VALUES (1, 'Дружба неподтверждённая'),
       (2, 'Дружба подтверждённая');

MERGE INTO rating_mpa (mpa_id, mpa_name)
KEY(mpa_id)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

MERGE INTO genre (genre_id, genre_name)
KEY(genre_id)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');


