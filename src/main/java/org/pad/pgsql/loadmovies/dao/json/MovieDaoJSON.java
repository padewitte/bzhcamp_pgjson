package org.pad.pgsql.loadmovies.dao.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pad.pgsql.loadmovies.dao.traditional.MovieDao;
import org.pad.pgsql.loadmovies.model.Movie;
import org.pad.pgsql.loadmovies.model.OtherInformations;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by padewitte on 20/04/2015.
 */
public class MovieDaoJSON extends MovieDao {


    public MovieDaoJSON(PGPoolingDataSource source) {
        super(source);
    }

    public void save(Movie movie) {
        String SQL = "insert into movies values (?, ?, ?, ?, ?)";
        System.out.println("Creating movie  = " + ToStringBuilder.reflectionToString(movie));
        jdbcTemplateObject.update(SQL, movie.getId(), movie.getTitle(), movie.getYear(), movie.getImdbId(), movie.getTmdbId());
    }

    public List<Movie> getAll() {


            ObjectMapper mapper = new ObjectMapper();
            final List<Movie> ret = new ArrayList<>();
            jdbcTemplateObject.query("select * from movies", new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    Movie aMovie = new Movie(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("year"));
                    aMovie.setImdbId(resultSet.getInt("imdbid"));
                    aMovie.setTmdbId(resultSet.getInt("tmdbid"));
                    try {
                        String jsoncolumn = resultSet.getString("otherInformations");
                        OtherInformations otherInformations = mapper.readValue(jsoncolumn, OtherInformations.class);
                        aMovie.setOtherInformations(otherInformations);
                    } catch (IOException e) {
                        //On est dans une démo :-)
                        e.printStackTrace();
                    }
                    ret.add(aMovie);
                }
            });


        return ret;
    }

    public void addOtherInformationsToMovie(Integer movieId,
                                            OtherInformations overallRating)
            throws JsonProcessingException {
        System.out.println("Updating movie  = " + movieId);

        String SQL = "update movies set otherInformations = ? where id = ?";
        //Using Jackson to convert additional field to JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final String ratingsAsString = mapper.writeValueAsString(overallRating);
        PreparedStatementSetter pstSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement)
                    throws SQLException {
                //Using a PGObject to store otherInformations
                PGobject dataObject = new PGobject();
                dataObject.setType("jsonb");
                dataObject.setValue(ratingsAsString);
                preparedStatement.setObject(1, dataObject);
                preparedStatement.setInt(2, movieId);
            }
        };
        jdbcTemplateObject.update(SQL, pstSetter);

    }

}
