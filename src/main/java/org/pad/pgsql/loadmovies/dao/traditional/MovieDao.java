package org.pad.pgsql.loadmovies.dao.traditional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pad.pgsql.loadmovies.model.Movie;
import org.pad.pgsql.loadmovies.model.OtherInformations;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by padewitte on 20/04/2015.
 */
public class MovieDao {

    private PGPoolingDataSource source;
    protected JdbcTemplate jdbcTemplateObject;

    public MovieDao(PGPoolingDataSource source){
        this.source = source;
        this.jdbcTemplateObject = new JdbcTemplate(source);
    }

    public void save(Movie movie){

        String SQL = "insert into movies values (?, ?, ?, ?, ?)";
        System.out.println("Creating movie  = " + ToStringBuilder.reflectionToString(movie));
        jdbcTemplateObject.update(SQL, movie.getId(), movie.getTitle(), movie.getYear(), movie.getImdbId(), movie.getTmdbId());


    }

    public List<Movie> getAll(){
        final List<Movie> ret = new ArrayList<>();
        jdbcTemplateObject.query("select * from movies", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                ret.add(new Movie(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("year")));
            }
        });
        return ret;
    }

}
