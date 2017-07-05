package com.example.movierama.events;

import com.example.movierama.models.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public class GotAllMoviesInTheaterEvent {


    /**
     * The Event of the getAllMoviesInTheaterRequest
     */
    public GotAllMoviesInTheaterEvent() {
    }

    public GotAllMoviesInTheaterEvent(List<Movie> movies) {
        this.movies = movies;
    }

    public GotAllMoviesInTheaterEvent(List<Movie> movies, int totalCount) {
        this.totalCount = totalCount;
        this.movies = movies;
    }

    public GotAllMoviesInTheaterEvent(List<Movie> movies, int totalCount, int paging) {
        this.totalCount = totalCount;
        this.movies = movies;
        this.paging = paging;
    }

    private List<Movie> movies;
    private int totalCount;
    private int paging;

    public List<Movie> getMovies() {
        if (movies == null)
            movies = new ArrayList<>();

        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPaging() {
        return paging;
    }

    public void setPaging(int paging) {
        this.paging = paging;
    }
}
