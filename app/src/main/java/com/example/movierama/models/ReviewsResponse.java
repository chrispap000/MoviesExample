package com.example.movierama.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 5/7/2017.
 */

public class ReviewsResponse {
    List<Review> reviews;

    public List<Review> getReviews() {
        if(reviews == null)
            reviews = new ArrayList<>();

        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
