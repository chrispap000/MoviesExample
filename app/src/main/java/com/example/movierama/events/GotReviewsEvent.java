package com.example.movierama.events;

import com.example.movierama.models.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 16/1/2016.
 */
public class GotReviewsEvent {

    /**
     * The Event of the getMovieReviewsByIdRequest
     */
    private List<Review> reviews;

    public GotReviewsEvent(){}

    public GotReviewsEvent(List<Review> reviews){
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        if(reviews == null)
            reviews = new ArrayList<>();

        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
