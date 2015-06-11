-- Drop index
rollback
DROP INDEX IDX_GIN_otherInformations;
DROP INDEX  IDX_GIN_otherInformations_opt;


-- Recherche JSON
EXPLAIN SELECT id, otherinformations->'nbOfRatings'
FROM movies
WHERE otherinformations @> '{"nbOfRatings" : 9}'::jsonb

EXPLAIN SELECT id, otherinformations->'nbOfRatings'
FROM movies
WHERE otherinformations ? 'nbOfRatings'



-- Création d'un index sur l'ensemble du document
CREATE INDEX IDX_GIN_otherInformations
ON movies
USING GIN (otherInformations );


-- Création d'un index sur l'ensemble du document
CREATE INDEX IDX_GIN_otherInformations_opt
ON movies
USING GIN (otherInformations jsonb_path_ops)

