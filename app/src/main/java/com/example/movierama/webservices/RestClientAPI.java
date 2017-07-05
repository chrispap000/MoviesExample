package com.example.movierama.webservices;

import com.example.movierama.models.Movie;
import com.example.movierama.models.MoviesResponse;
import com.example.movierama.models.ReviewsResponse;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public interface RestClientAPI {

    @GET("/api/public/v1.0/lists/movies/in_theaters.json")
    void getAllMoviesInTheatreRequest(@Query("apikey") String apikey, @Query("page_limit") int page_limit, @Query("page") int page, @Query("country") String country,
                                      Callback<MoviesResponse> response);

    @GET("/api/public/v1.0/movies.json")
    void getAllMoviesByTitleRequest(@Query("apikey") String apikey, @Query("page_limit") int page_limit, @Query("page") int page, @Query("q") String q,
                                    Callback<MoviesResponse> response);

    @GET("/api/public/v1.0/movies/{id}.json")
    void getMovieRequest(@Query("apikey") String apikey, @Path("id") String id,
                         Callback<Movie> response);

    @GET("/api/public/v1.0/movies/{id}/reviews.json")
    void getMovieReviewsRequest(@Query("apikey") String apikey, @Path("id") String id,
                                Callback<ReviewsResponse> response);

    @GET("/api/public/v1.0/movies/{id}/similar.json")
    void getMovieSimilarRequest(@Query("apikey") String apikey, @Path("id") String id, @Query("limit") int limit,
                                Callback<MoviesResponse> response);
}
