DELETE from movies where id = 666666;

INSERT INTO movies(id, name, year, imdbid, tmdbid, otherInformations)
VALUES (666666, 'Diabolic movie', 1966, 666, 6666, '{"ratings" : [{"userId" : 4, "rating" : 3.5}]}');ROLLBACK;
ALTER TABLE movies ADD COLUMN otherInformations jsonb;
-- Lecture brute d’un champ JSON
SELECT otherInformations
FROM movies
WHERE otherinformations IS NOT NULL
LIMIT 2


-- Requête avec like
SELECT otherinformations
FROM movies
WHERE otherinformations LIKE '%tags%'
LIMIT 2


-- Requête avec like et cast
SELECT otherinformations
FROM movies
WHERE otherinformations::text LIKE '%tags%'
LIMIT 2


--Filtrage sur un champ
SELECT otherinformations::text
FROM movies
WHERE (otherinformations->>'averageRating')::float > 4 limit 2



-- Lecture texte d'un élément de tableau
SELECT otherinformations::jsonb->'tags'->>3
FROM movies
WHERE jsonb_array_length(otherinformations::jsonb->'tags') > 3
LIMIT 2

-- Lecture JSON d'un élément de tableau
SELECT otherinformations::jsonb->'tags'->3->'tag'
FROM movies
WHERE jsonb_array_length(otherinformations::jsonb->'tags') > 3
LIMIT 2



-- Tester l’existence d’un champ
SELECT jsonb_typeof(otherinformations->'nbOfRatings')
FROM movies
WHERE otherinformations ? 'nbOfRatings'


-- Recherche JSON
SELECT id, otherinformations->'nbOfRatings' 
FROM movies
WHERE
  otherinformations @> '{"nbOfRatings" : 9}'::jsonb



-- Lecture avec chemin en pseudo JSON
-- Recherche du champ tag du cinquième élément du tableau tags
SELECT otherinformations #> '{tags, 5, tag}'
FROM movies
WHERE jsonb_array_length(otherinformations::jsonb->'tags') > 4
LIMIT 2



-- Affichage d'une liste de clefs JSON
SELECT distinct(jsonb_object_keys(otherinformations)), count(*)
FROM movies
group by jsonb_object_keys(otherinformations)
-- Attention limit le nb de clef et non le nombre d'objects
LIMIT 2

-- Affichage d'une liste de clefs JSON
-- Equivalent MongoDB du unwind
SELECT jsonb_array_elements_text(otherinformations->'tags')
FROM movies







select * from movies where id = 666666;

SELECT row_to_json(movies.*)
FROM movies
where id = 666666
LIMIT 1
