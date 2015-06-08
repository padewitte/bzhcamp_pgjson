----------------------------------------------------
-- Creation script of the traditionnal schema
----------------------------------------------------
DROP TABLE movies, ratings, tags;
CREATE TABLE movies
(
  id integer,
  name character varying(200),
  year character varying(4),
  imdbid integer,
  tmdbid integer,
  CONSTRAINT "PK_MOVIES" PRIMARY KEY (id)
);


CREATE TABLE ratings
(
  userId integer,
  movieId integer,
  rating float,
  ratDate date,
  CONSTRAINT "PK_RATINGS" PRIMARY KEY (userId, movieId)
) ;


CREATE TABLE tags
(
  userId integer,
  movieId integer,
  tag character varying(400),
  date date,
  CONSTRAINT "PK_TAGS" PRIMARY KEY (userId, movieId,tag)
);

commit;


