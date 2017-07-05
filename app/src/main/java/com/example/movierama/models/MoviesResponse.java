package com.example.movierama.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 5/7/2017.
 */

public class MoviesResponse {

    private int total;
    private List<Movie> movies;

    public List<Movie> getMovies() {
        if(movies == null)
            movies= new ArrayList<>();

        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }



}
