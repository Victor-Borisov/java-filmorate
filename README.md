# java-filmorate
Filmorate project for Yandex.Practicum (java-developer course)

### ER Diagram:

<img src="./ER.png" width="1101" alt="Entity relationship" />

### Запросы:
Список всех пользователей:

 ```
 SELECT user_id,
	name,
	email,
	login,
	birthday
 FROM user;
 ```

Список user_id друзей пользователя с user_id = 1:

 ```
 SELECT user_id_2 
 FROM friendship 
 WHERE user_id_1 = 1;
 ```

Список всех фильмов:

 ```
 SELECT f.film_id,
	f.name,
	f.description,
	f.release_date,
	f.duration,
	mpa.mpa_name as rating_mpa
 FROM film f
 LEFT JOIN rating_mpa mpa;
 ```

Жанры фильма с film_id = 1:

 ```
 SELECT fg.genre_id,
   fg.genre_name
 FROM film_genre fg
 LEFT JOIN genre g ON g.genre_id = fg.genre_id
 WHERE fg.film_id = 1;
 ```

Топ 10 наиболее популярных фильмов по лайкам:

 ```
 SELECT f.film_id, 
    f.name,
    f.description, 
    f.release_date, 
    f.duration, 
    m.mpa_id, 
    m.mpa_name, 
    count(fl.film_id) as like_count 
 FROM films f 
 LEFT JOIN rating_mpa m ON m.mpa_id = f.rating_mpa_id 
 LEFT JOIN film_like fl ON fl.film_id = f.film_id 
 GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa_name 
 ORDER BY count(fl.film_id) desc 
 LIMIT 10;
 ```

Список общих друзей для пользователей 1 и 2:

 ```
 SELECT u.user_id
 FROM users u, friendship f, friendship o 
 WHERE u.user_id = f.user_id_2 
 AND u.user_id = o.user_id_2 
 AND f.user_id_1 = 1
 AND o.user_id_1 = 2;
  ```
