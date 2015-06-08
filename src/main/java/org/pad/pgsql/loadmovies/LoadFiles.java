package org.pad.pgsql.loadmovies;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.pad.pgsql.loadmovies.dao.json.MovieDaoJSON;
import org.pad.pgsql.loadmovies.dao.traditional.MovieDao;
import org.pad.pgsql.loadmovies.dao.traditional.RatingDao;
import org.pad.pgsql.loadmovies.dao.traditional.TagDao;
import org.pad.pgsql.loadmovies.model.Movie;
import org.pad.pgsql.loadmovies.model.OtherInformations;
import org.pad.pgsql.loadmovies.model.Rating;
import org.pad.pgsql.loadmovies.model.Tag;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.util.LinkedMultiValueMap;

import java.io.FileReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Loading data from http://grouplens.org/datasets/movielens/ in postgres with two schema design :
 * - Traditional one with three tables
 * - JSON one with all informations in a the movie table
 * <p>
 * "THE BEER-WARE LICENSE" (Revision 42): wrote this file. As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think this stuff is worth it, you can buy me a beer in return Poul-Henning Kamp
 */
public class LoadFiles {

    public static void main(String[] args) throws Exception {
        loadMoviesAndLinks();
        loadRatings();
    }


    /**
     * Filter movieId to speed up the loading process.
     *
     * @param movieId
     * @return
     */
    private static boolean keepId(Integer movieId) {
        return movieId % 10 == 0;
    }

    /**
     * Load movies from csv file and save them in DB.
     *
     * @throws Exception
     */
    private static void loadMoviesAndLinks() throws Exception {
        MovieDao movieDao = new MovieDao(DS);
        Map<Integer, Integer[]> moviesLinks = new HashMap<>();
        //Loads all links informations in memory to enrich afterwards movies
        CSVParser parser = new CSVParser(new FileReader("C:\\PRIVE\\SRC\\ml-20m\\links.csv"), CSVFormat.EXCEL.withHeader());
        for (CSVRecord link : parser) {
            Integer movieId = Integer.parseInt(link.get("movieId"));
            if (keepId(movieId)) {
                System.out.println("Parsing line " + link.toString());
                Integer[] otherIds = new Integer[2];
                otherIds[0] = Integer.parseInt(link.get("imdbId"));
                if (StringUtils.isNoneEmpty(link.get("tmdbId"))) {
                    otherIds[1] = Integer.parseInt(link.get("tmdbId"));
                }
                moviesLinks.put(movieId, otherIds);
            }
        }

        //Read movie file
        final Reader reader = new FileReader("C:\\PRIVE\\SRC\\ml-20m\\movies.csv");
        parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

        for (CSVRecord record : parser) {
            //build a movie object from record
            Integer movieId = Integer.parseInt(record.get("movieId"));
            if (keepId(movieId)) {
                String title = record.get("title");
                String genres = record.get("genres");
                //Splitting title to extract the date
                String movieDate = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(title, "("), ")");
                String movieName = null;
                if (StringUtils.isNumeric(movieDate)) {
                    movieName = StringUtils.substringBeforeLast(title, "(");
                } else {
                    movieName = title;
                    movieDate = null;
                }

                System.out.println(movieName + " - " + movieDate);
                Movie movieToAdd = new Movie(movieId, movieName, movieDate);

                //Enrich movie with links
                Integer[] additionalIds = moviesLinks.get(movieId);
                if (additionalIds != null) {
                    movieToAdd.setImdbId(additionalIds[0]);
                    movieToAdd.setTmdbId(additionalIds[1]);
                }

                //Save in database
                movieDao.save(movieToAdd);
            }
        }
    }


    /**
     * Read tags and load them in a multivalue map.
     *
     * @return MultivalueMap with key movieId and values all tags.
     * @throws Exception it is only a demo.
     */
    private static LinkedMultiValueMap<Integer, Tag> readTags() throws Exception {
        TagDao tagDao = new TagDao(DS);
        LinkedMultiValueMap<Integer, Tag> tags = new LinkedMultiValueMap();
        final Reader reader = new FileReader("C:\\PRIVE\\SRC\\ml-20m\\tags.csv");
        CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
        for (CSVRecord record : parser) {

            Integer movieId = Integer.parseInt(record.get("movieId"));
            Integer userId = Integer.parseInt(record.get("userId"));
            if (keepId(movieId) && keepId(userId)) {
                //CSV Header : userId,movieId,tag,timestamp
                Tag newTag = new Tag();
                newTag.setUserId(userId);
                newTag.setMovieId(movieId);
                newTag.setTag(record.get("tag"));
                newTag.setDate(new Date(Long.parseLong(record.get("timestamp")) * 1000));
                //Adding to map for json loading
                tags.add(newTag.getMovieId(), newTag);
                //Saving in tag table
                //tagDao.save(newTag);
            }
        }
        return tags;
    }

    /**
     * Load ratings and enrich movies with tags informations before updating the related movie.
     *
     * @throws Exception
     */
    private static void loadRatings() throws Exception {
        //MultivalueMap with key movieId and values all tags
        LinkedMultiValueMap<Integer, Tag> tags = readTags();

        //MultivalueMap with key movieId and values all ratings
        LinkedMultiValueMap<Integer, Rating> ratings = new LinkedMultiValueMap();

        //"userId,movieId,rating,timestamp
        final Reader reader = new FileReader("C:\\PRIVE\\SRC\\ml-20m\\ratings.csv");
        CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
        RatingDao ratingDao = new RatingDao(DS);
        for (CSVRecord record : parser) {
            Integer movieId = Integer.parseInt(record.get("movieId"));
            Integer userId = Integer.parseInt(record.get("userId"));
            if (keepId(movieId) && keepId(userId)) {
                //Building a rating object.
                Rating rating = new Rating();
                rating.setUserId(userId);
                rating.setMovieId(movieId);
                rating.setRating(Float.parseFloat(record.get("rating")));
                rating.setDate(new Date(Long.parseLong(record.get("timestamp")) * 1000));
                //Add for json saving
                ratings.add(rating.getMovieId(), rating);
                //traditional saving
                //ratingDao.save(rating);
            }
        }
        MovieDaoJSON movieDaoJSON = new MovieDaoJSON(DS);
        ratings.entrySet().stream().forEach((integerListEntry -> {
            //Building other information objects
            OtherInformations overRatings = new OtherInformations();
            overRatings.setRatings(integerListEntry.getValue());
            overRatings.computeMean();
            overRatings.setTags(tags.get(integerListEntry.getKey()));
            movieDaoJSON.addRatingsToMovie(integerListEntry.getKey(), overRatings);
        }));

    }


    //Static pool for demo purpose
    private static PGPoolingDataSource DS = getDS();

    //Creating the pool
    private static PGPoolingDataSource getDS() {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("My custom PG datasource");
        source.setServerName("localhost");
        source.setDatabaseName("json_test");
        source.setUser("user");
        source.setPassword("user");
        source.setMaxConnections(10);
        source.setCurrentSchema("public");
        return source;
    }
}
