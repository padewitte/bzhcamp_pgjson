-- Lecture brute d'un champ
SELECT otherinformations
FROM movies
WHERE otherinformations IS NOT NULL
LIMIT 2

-- Requete avec like
SELECT otherinformationsFROM moviesWHERE otherinformations LIKE '%tags%'LIMIT 2

-- Requete avec like
SELECT otherinformations
FROM movies
WHERE otherinformations::text LIKE '%tags%'
LIMIT 2

--Lecture d'un champ comme du JSON et comme du texte
SELECT otherinformations->'averageRating' FROM movies WHERE (otherinformations->>'averageRating')::float > 4


-- Lecture texte d'un élément de tableau
SELECT otherinformations->'tags'->>3 FROM movies LIMIT 2

-- Lecture JSON d'un élément de tableau
SELECT otherinformations->'tags'->3->'tag'
FROM movies
LIMIT 2

-- Lecture avec chemin en pseudo JSON
SELECT otherinformations#>'{tags, 5, tag}'
FROM movies
LIMIT 2


-- Recherche JSON
SELECT id, otherinformations->'nbOfRatings'
FROM movies
WHERE otherinformations @> '{"nbOfRatings" : 50}'::jsonb


SELECT count(*)
FROM movies
WHERE otherinformations ? 'nbOfRatings'


SELECT row_to_json(movies.*)
FROM movies
LIMIT 1


-- Update avec chemin en pseudo JSON
UPDATE movies
SET otherinformations = jsonb_replace(otherinformations, '{"tags",3,"tag"}', '{"SUPER"}'::jsonb)
WHERE id = 1


select otherinformations::jsonb->'tags'->>3->'tag' from movies

select otherinformations::jsonb->'tags'->3->'tag' from movies where (otherinformations->'averageRating')::text::float > 4
select otherinformations->'tags'->3->'tag' from movies where (otherinformations->>'averageRating')::float > 4

select otherinformations::jsonb->'averageRating' from movies where (otherinformations->>'averageRating')::float > 4
select otherinformations::jsonb#>'tags' from movies where (otherinformations->'averageRating')::text::float > 4

select * from movies limit 50

rollback
select row_to_json(movies.*) from movies where otherinformations is not null limit 1
select row_to_json(row(name, year, imdbid)) from movies where otherinformations is not null limit 1


row_to_json(

select ratings::json->'mean', * from movies where ratings @> '{"mean" : 3.5}'
select ratings::json->'mean', * from movies where ratings is not null and ratings::json->'mean' == 3.5


select * from ratings where ratings is not null and ratings::jsonb @> '{"mean":3.5}'::jsonb


>	int	Get JSON array element (indexed from zero)	'[{"a":"foo"},{"b":"bar"},{"c":"baz"}]'::json->2	{"c":"baz"}
->	text	Get JSON object field by key	'{"a": {"b":"foo"}}'::json->'a'	{"b":"foo"}
->>	int	Get JSON array element as text	'[1,2,3]'::json->>2	3
->>	text	Get JSON object field as text	'{"a":1,"b":2}'::json->>'b'	2
#>	text[]	Get JSON object at specified path	'{"a": {"b":{"c": "foo"}}}'::json#>'{a,b}'	{"c": "foo"}
#>>	text[]	Get JSON object at specified path as text	'{"a":[1,2,3],"b":[4,5,6]}'::json#>>'{a,2}'	3