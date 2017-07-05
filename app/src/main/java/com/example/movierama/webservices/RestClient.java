package com.example.movierama.webservices;

import android.content.Context;
import android.util.Log;

import com.example.movierama.models.MoviesResponse;
import com.example.movierama.models.ReviewsResponse;
import com.google.gson.JsonElement;
import com.example.movierama.events.GotAllMoviesByTitleEvent;
import com.example.movierama.events.GotAllMoviesInTheaterEvent;
import com.example.movierama.events.GotMovieEvent;
import com.example.movierama.events.GotReviewsEvent;
import com.example.movierama.events.GotSimilarMoviesEvent;
import com.example.movierama.helpers.JSONParser;
import com.example.movierama.models.Movie;
import com.example.movierama.models.Review;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by iosdevelopment on 1/15/16.
 */
public class RestClient {

    public static final String ENDPOINT = "http://api.rottentomatoes.com";
    public static final String API_KEY = "YOUR_API_KEY";

    private static RestClient mSingleton;
    private RestClientAPI mAPI;
    private Context mContext;

    /**
     * Get RestClient's Instance
     *
     * @return the application instance
     */
    public static RestClient getInstance() {
        return mSingleton;
    }


    /**
     * Constructor for Web Services
     */
    public RestClient(Context context) {
        this.mSingleton = this;

        RestAdapter adapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ENDPOINT)
                .build();

        // Here is our base API to use!!!!
        mAPI = adapter.create(RestClientAPI.class);

        mContext = context;

    }

    /**
     * Get all movies in theater
     * @param page_limit maximum number of movies should be returned
     * @param page number of paging
     * @param country the country desired
     */
    public void getAllMoviesInTheaterRequest(int page_limit, final int page, String country) {
        mAPI.getAllMoviesInTheatreRequest(API_KEY, page_limit, page, country, new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                try {
                    GotAllMoviesInTheaterEvent event = new GotAllMoviesInTheaterEvent(moviesResponse.getMovies(), moviesResponse.getTotal(), page);
                    EventBus.getDefault().post(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", error.toString());
            }
        });
    }

    /**
     * Get all movies with title like the filter text
     * @param page_limit maximum number of movies should be returned
     * @param page number of paging
     * @param title the filter text
     */
    public void getAllMoviesByTitleRequest(int page_limit, final int page, String title) {
        mAPI.getAllMoviesByTitleRequest(API_KEY, page_limit, page, title, new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                try {

                    GotAllMoviesByTitleEvent event = new GotAllMoviesByTitleEvent(moviesResponse.getMovies(), moviesResponse.getTotal(), page);
                    EventBus.getDefault().post(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", error.toString());
            }
        });
    }

    /**
     * Get movie's detail by its ID
     * @param movieId the movie ID
     */
    public void getMovieByIdRequest(String movieId) {
        mAPI.getMovieRequest(API_KEY, movieId, new Callback<Movie>() {
            @Override
            public void success(Movie movie, Response response) {
                try {

                    GotMovieEvent event = new GotMovieEvent(movie);
                    EventBus.getDefault().post(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", error.toString());
            }
        });
    }

    /**
     * Get movie's reviews by its ID
     * @param movieId the movie ID
     */
    public void getMovieReviewsByIdRequest(String movieId) {
        mAPI.getMovieReviewsRequest(API_KEY, movieId, new Callback<ReviewsResponse>() {
            @Override
            public void success(ReviewsResponse reviewsResponse, Response response) {
                try {
                    GotReviewsEvent event = new GotReviewsEvent(reviewsResponse.getReviews());
                    EventBus.getDefault().post(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", error.toString());
            }
        });
    }

    /**
     * Get movie's similar movies by movie's ID
     * @param movieId the movie ID
     */
    public void getMovieSimilarByIdRequest(String movieId) {
        mAPI.getMovieSimilarRequest(API_KEY, movieId, 5, new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse moviesResponse, Response response) {
                try {
                    GotSimilarMoviesEvent event = new GotSimilarMoviesEvent(moviesResponse.getMovies());
                    EventBus.getDefault().post(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", error.toString());
            }
        });
    }
}
