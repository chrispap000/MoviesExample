package com.example.movierama.events;

import com.example.movierama.models.Movie;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public class GotMovieEvent {

    /**
     * The Event of the getMovieByIdRequest
     */
    private Movie movie;

    public GotMovieEvent() {
    }

    public GotMovieEvent(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
