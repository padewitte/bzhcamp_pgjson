package org.pad.pgsql.loadmovies.dao.traditional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pad.pgsql.loadmovies.model.Rating;
import org.pad.pgsql.loadmovies.model.Tag;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by padewitte on 05/05/2015.
 */
public class TagDao {

    private PGPoolingDataSource source;
    private JdbcTemplate jdbcTemplateObject;

    public TagDao(PGPoolingDataSource source){
        this.source = source;
        this.jdbcTemplateObject = new JdbcTemplate(source);
    }


    public void save(Tag tag){
        String SQL = "insert into tags values (?, ?, ?, ?)";
        System.out.println("Creating tag  = " + ToStringBuilder.reflectionToString(tag));
        jdbcTemplateObject.update(SQL, tag.getMovieId(), tag.getUserId(), tag.getTag(), tag.getDate());
    }
}
