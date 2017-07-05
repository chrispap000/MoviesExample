package com.example.movierama.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public class Movie {

    private String _id;
    private String title;
    private int year;
    private String poster;
    private String synopsis;
    private int runtime;
    private String releaseDateInTheater;
    private int ratingAudience;
    private List<String> cast;
    private List<String> director;
    private List<String>  genres;
    private List<String> similarMovies;
    private List<String> reviews;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getCast() {
        if (cast == null)
            cast = new ArrayList<>();

        return cast;
    }

    public void setCast(List<String> cast) {
        this.cast = cast;
    }

    public List<String> getDirector() {
        if (director == null)
            director = new ArrayList<>();

        return director;
    }

    public void setDirector(List<String> director) {

        this.director = director;
    }

    public List<String> getGenres() {

        if (genres == null)
            genres = new ArrayList<>();

        return genres;
    }

    public void setGenres(List<String> genres) {


        this.genres = genres;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getRatingAudience() {
        return ratingAudience;
    }

    public void setRatingAudience(int ratingAudience) {
        this.ratingAudience = ratingAudience;
    }

    public String getReleaseDateInTheater() {
        return releaseDateInTheater;
    }

    public void setReleaseDateInTheater(String releaseDateInTheater) {
        this.releaseDateInTheater = releaseDateInTheater;
    }

    public List<String> getReviews() {
        if (reviews == null)
            reviews = new ArrayList<>();

        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getSimilarMovies() {
        if (similarMovies == null)
            similarMovies = new ArrayList<>();

        return similarMovies;
    }

    public void setSimilarMovies(List<String> similarMovies) {
        this.similarMovies = similarMovies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

}
