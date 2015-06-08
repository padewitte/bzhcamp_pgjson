package org.pad.pgsql.loadmovies.dao.traditional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pad.pgsql.loadmovies.model.Movie;
import org.pad.pgsql.loadmovies.model.Rating;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by padewitte on 21/04/2015.
 */
public class RatingDao {
    private PGPoolingDataSource source;
    private JdbcTemplate jdbcTemplateObject;

    public RatingDao(PGPoolingDataSource source){
        this.source = source;
        this.jdbcTemplateObject = new JdbcTemplate(source);
    }


    public void save(Rating rating){
        String SQL = "insert into ratings values (?, ?, ?, ?)";
        System.out.println("Creating rating  = " + ToStringBuilder.reflectionToString(rating));
        jdbcTemplateObject.update(SQL, rating.getMovieId(), rating.getUserId(), rating.getRating(), rating.getDate());
    }
}
