DELETE from movies where id = 666666;


INSERT INTO movies(id, name, year, imdbid, tmdbid, otherInformations)
VALUES (666666, 'Diabolic movie', 1966, 666, 6666, '{"ratings" : [{"userId" : 4, "rating" : 3.5}]}');

select * from movies where id = 666666;

SELECT row_to_json(movies.*)
FROM movies
LIMIT 1
