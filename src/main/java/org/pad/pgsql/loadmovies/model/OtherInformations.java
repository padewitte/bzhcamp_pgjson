package org.pad.pgsql.loadmovies.model;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by padewitte on 04/05/2015.
 */
public class OtherInformations {

    private List<Rating> ratings;

    private List<Tag> tags;

    private Double averageRating;

    private Integer nbOfRatings;

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNbOfRatings() {
        return nbOfRatings;
    }

    public void setNbOfRatings(Integer nbOfRatings) {
        this.nbOfRatings = nbOfRatings;
    }

    public void computeMean(){
        nbOfRatings = ratings.size();
        Optional<Float> mean3 =  ratings.stream().map(rating -> rating.getRating()).reduce((aFloat, aFloat2) -> aFloat + aFloat2 / 2);
        Double mean2 = ratings.stream().collect(Collectors.averagingDouble(value -> value.getRating()));
        System.out.println(mean3 +" : " +mean2);
        averageRating = mean2;
        //Keep only 10 ratings to ease query
        ratings = ratings.subList(0,Math.min(10, ratings.size()));
    }
}
