use moviedb;

-- DROP PROCEDURE add_movie;

DELIMITER $$

CREATE PROCEDURE add_movie
(IN id varchar(10),
 IN title varchar(100),
 IN year int,
 IN director varchar(100),
 IN rating float,
 IN star varchar(100),
 IN genre varchar(32))

BEGIN

INSERT INTO movies values(id, title, year, director);
INSERT INTO ratings values(id, rating, 1);


SELECT @star_id := stars.id FROM stars WHERE stars.name = star;
INSERT INTO stars_in_movies values(@star_id, id);

SELECT @genre_id := genres.id FROM genres WHERE genres.name = genre;
INSERT INTO genres_in_movies values(@genre_id, id);

END
$$
