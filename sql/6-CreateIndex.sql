-- Drop index
DROP INDEX IDX_JSON_otherInformations_tag_tags;

-- Creatin d'un index pour recherche par nb de ratings
CREATE INDEX IDX_JSON_otherInformations_tag_tags
ON movies ((otherInformations #> '{nbOfRatings}'));

-- Création d'un index sur l'ensemble du document
CREATE INDEX IDX_GIN_otherInformations
ON movies
USING GIN (otherInformations);

DROP INDEX  IDX_GIN_otherInformations_opt;
-- Création d'un index sur l'ensemble du document
CREATE INDEX IDX_GIN_otherInformations_opt 
ON movies 
USING GIN (otherInformations jsonb_path_ops);


select count(*) from movies where (otherInformations#>> '{nbOfRatings}')::float > 5000 limit 2

select  (otherInformations #>> '{tags}')::jsonb from movies where otherInformations is not null limit 2
select  id, jsonb_array_elements((otherInformations #>> '{tags}')::jsonb)->'tag' from movies where otherInformations is not null limit 2

select id, otherInformations from movies where otherInformations #>> '{tags,0,tag}' LIKE '%space%' limit 10

select id, otherInformations from movies where otherInformations ?