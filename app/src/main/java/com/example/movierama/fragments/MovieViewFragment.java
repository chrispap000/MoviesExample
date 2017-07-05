package com.example.movierama.fragments;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.example.movierama.R;
import com.example.movierama.events.GotMovieEvent;
import com.example.movierama.events.GotReviewsEvent;
import com.example.movierama.events.GotSimilarMoviesEvent;
import com.example.movierama.helpers.GeneralUtils;
import com.example.movierama.models.Movie;
import com.example.movierama.models.Review;
import com.example.movierama.services.NotificationPublisher;
import com.example.movierama.webservices.RestClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieViewFragment extends MovieRamaBaseFragment {

    //Variables
    Movie mMovie;


    //Binding Views
    @Bind(R.id.progressBarLayout)
    LinearLayout mProgressBarLayout;

    @Bind(R.id.posterImageView)
    ImageView mPosterImageView;

    @Bind(R.id.dateTextView)
    TextView mDateTextView;

    @Bind(R.id.synopsisTextView)
    TextView mSynopsisTextView;

    @Bind(R.id.directorTextView)
    TextView mDirectorTextView;

    @Bind(R.id.castTextView)
    TextView mCastTextView;

    @Bind(R.id.genreTextView)
    TextView mGenreTextView;

    @Bind(R.id.remindButton)
    Button mRemindButton;

    @Bind(R.id.reviewLayout)
    LinearLayout mReviewLayout;

    @Bind(R.id.similarLayout)
    LinearLayout mSimilarLayout;


    //Binding View Events
    @OnClick(R.id.remindButton)
    void remindButtonClicked() {
        scheduleNotification(getNotification(), mMovie.getReleaseDateInTheater());
    }

    public MovieViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_view, container, false);

        //ButterKnife bind for injecting the views
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Register EventBus
        EventBus.getDefault().register(this);

        //Get movie ID passed from list
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            String movieId = bundle.getString("id");
            RestClient.getInstance().getMovieByIdRequest(movieId);
            RestClient.getInstance().getMovieReviewsByIdRequest(movieId);
            RestClient.getInstance().getMovieSimilarByIdRequest(movieId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        //Unregister EventBus
        EventBus.getDefault().unregister(this);
    }

    private void drawMovie(Movie movie) {
        getActivity().setTitle(movie.getTitle());

        if (GeneralUtils.stringIsNullOrEmpty(movie.getSynopsis()) == false)
            mSynopsisTextView.setText(movie.getSynopsis());
        else
            mSynopsisTextView.setVisibility(View.GONE);

        if (GeneralUtils.stringIsNullOrEmpty(movie.getReleaseDateInTheater()) == false) {
            mDateTextView.setText(getDateFormatted(movie.getReleaseDateInTheater()));
            mRemindButton.setVisibility((checkIfDateIsInTheFuture(movie.getReleaseDateInTheater()) ? View.VISIBLE : View.GONE));
        } else {
            mDateTextView.setVisibility(View.GONE);
        }

        if (movie.getCast() != null && movie.getCast().size() > 0)
            mCastTextView.setText(getString(R.string.movie_view_fragment_cast) + " " + GeneralUtils.stringToShowFromStringList(movie.getCast()));
        else
            mCastTextView.setVisibility(View.GONE);

        if (movie.getDirector() != null && movie.getDirector().size() > 0)
            mDirectorTextView.setText(getString(R.string.movie_view_fragment_director) + " " + GeneralUtils.stringToShowFromStringList(movie.getDirector()));
        else
            mDirectorTextView.setVisibility(View.GONE);

        if (movie.getGenres() != null && movie.getGenres().size() > 0)
            mGenreTextView.setText(getString(R.string.movie_view_fragment_genre) + " " + GeneralUtils.stringToShowFromStringList(movie.getGenres()));
        else
            mGenreTextView.setVisibility(View.GONE);

        Picasso.with(getActivity()).load(movie.getPoster()).into(mPosterImageView);
    }

    private String getDateFormatted(String dateString) {
        String result = dateString;

        SimpleDateFormat receiveDT = new SimpleDateFormat("yyyy-M-dd");
        SimpleDateFormat showDT = new SimpleDateFormat("MMMM dd, yyyy");
        try {
            Date date = receiveDT.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            result = showDT.format(cal.getTime());
        } catch (ParseException e) {
        } catch (Exception ex) {
        }

        return result;
    }

    private boolean checkIfDateIsInTheFuture(String dateString) {
        Calendar c = Calendar.getInstance();

        // set the calendar to start of today
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        SimpleDateFormat receiveDT = new SimpleDateFormat("yyyy-M-dd");
        try {
            Date dateSpecified = receiveDT.parse(dateString);
            if (dateSpecified.after(today)) {
                return true;
            }
        } catch (ParseException e) {
        } catch (Exception ex) {
        }

        return false;
    }

    private void drawReviews(List<Review> reviews) {
        if (reviews != null && reviews.size() > 0) {
            for (Review r : reviews) {
                View reviewView = getActivity().getLayoutInflater().inflate(R.layout.review_row_layout, null);

                TextView quoteTextView = (TextView) reviewView.findViewById(R.id.quoteTextView);
                TextView linkTextView = (TextView) reviewView.findViewById(R.id.linkTextView);

                if (GeneralUtils.stringIsNullOrEmpty(r.getQuote()) == false)
                    quoteTextView.setText(r.getQuote());
                else
                    quoteTextView.setVisibility(View.GONE);

                if (GeneralUtils.stringIsNullOrEmpty(r.getLink_review()) == false)
                    linkTextView.setText(r.getLink_review());
                else
                    linkTextView.setVisibility(View.GONE);

                mReviewLayout.addView(reviewView);
            }
        } else {
            mReviewLayout.setVisibility(View.GONE);
        }
    }

    private void drawSimilarMovies(List<Movie> movies) {
        if (movies != null && movies.size() > 0) {
            for (Movie m : movies) {
                View reviewView = getActivity().getLayoutInflater().inflate(R.layout.similar_movie_row_layout, null);

                TextView similarTitleTextView = (TextView) reviewView.findViewById(R.id.similarTitleTextView);

                if (GeneralUtils.stringIsNullOrEmpty(m.getTitle()) == false)
                    similarTitleTextView.setText(m.getTitle());
                else
                    similarTitleTextView.setVisibility(View.GONE);

                mSimilarLayout.addView(reviewView);
            }
        } else {
            mSimilarLayout.setVisibility(View.GONE);
        }
    }

    private void scheduleNotification(Notification notification, String date) {

        try {

            Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Add the hour to the date, so that the notification will be fired at 10:00:00 of the specified date
            String d = date + " 10:00:00.000";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss.SSS");

            Date dateSpecified = format.parse(d);
            dateSpecified.getTime();
            long futureInMillis = dateSpecified.getTime();

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);

            showAddedReminderDialog();
        } catch (Exception e) {

        }
    }

    private void showAddedReminderDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.movie_view_fragment_reminder_alert_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private Notification getNotification() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setContentTitle(mMovie.getTitle());
        builder.setAutoCancel(true);
        builder.setSound(alarmSound);
        builder.setContentText(getString(R.string.notification_content));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    //Event Bus Events - RestClient Event
    public void onEvent(GotMovieEvent event) {
        mProgressBarLayout.setVisibility(View.GONE);

        if (event.getMovie() != null) {
            mMovie = event.getMovie();
            drawMovie(mMovie);
        }
    }

    public void onEvent(GotSimilarMoviesEvent event) {
        mProgressBarLayout.setVisibility(View.GONE);

        if (event.getMovies() != null) {
            drawSimilarMovies(event.getMovies());
        }
    }

    public void onEvent(GotReviewsEvent event) {
        mProgressBarLayout.setVisibility(View.GONE);

        if (event.getReviews() != null) {
            drawReviews(event.getReviews());
        }
    }
}
