package com.example.movierama.events;

import com.example.movierama.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public class GotSimilarMoviesEvent {

    /**
     * The Event of the getMovieSimilarByIdRequest
     */
    public GotSimilarMoviesEvent() {
    }

    public GotSimilarMoviesEvent(List<Movie> movies) {
        this.movies = movies;
    }

    private List<Movie> movies;

    public List<Movie> getMovies() {
        if (movies == null)
            movies = new ArrayList<>();

        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

}
