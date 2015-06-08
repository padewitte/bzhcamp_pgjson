package org.pad.pgsql.loadmovies.model;

/**
 * Created by padewitte on 20/04/2015.
 */
public class Movie {

    private Integer id;
    private String year;
    private String title;
    private Integer imdbId;
    private Integer tmdbId;
    private OtherInformations otherInformations;

    public Movie(Integer id, String title, String year) {
        this.id = id;
        this.year = year;
        this.title = title;
    }

    public Movie() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImdbId() {
        return imdbId;
    }

    public void setImdbId(Integer imdbId) {
        this.imdbId = imdbId;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public OtherInformations getOtherInformations() {
        return otherInformations;
    }

    public void setOtherInformations(OtherInformations otherInformations) {
        this.otherInformations = otherInformations;
    }
}
