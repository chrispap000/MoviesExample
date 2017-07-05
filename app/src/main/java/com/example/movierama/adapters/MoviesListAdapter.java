package com.example.movierama.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.example.movierama.R;
import com.example.movierama.helpers.GeneralUtils;
import com.example.movierama.models.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Chris on 16/1/2016.
 */
public class MoviesListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> mMovies;

    public MoviesListAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    /**
     * Add new items to the adapter
     *
     * @param items list of new items
     */
    public void addItems(List<Movie> items) {
        mMovies.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Clear all items
     */
    public void clearItems() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int i) {
        return mMovies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Movie movie = mMovies.get(i);

        View rowView = view;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(R.layout.movies_row_layout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
            viewHolder.dateTextView = (TextView) rowView.findViewById(R.id.dateTextView);
            viewHolder.runtimeTextView = (TextView) rowView.findViewById(R.id.runtimeTextView);
            viewHolder.ratingRatingBar = (RatingBar) rowView.findViewById(R.id.ratingRatingBar);
            viewHolder.posterImageView = (ImageView) rowView.findViewById(R.id.posterImageView);
            viewHolder.castTextView = (TextView) rowView.findViewById(R.id.castTextView);

            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.titleTextView.setText(movie.getTitle());

        if (GeneralUtils.stringIsNullOrEmpty(movie.getReleaseDateInTheater()) == false) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(getDateFormatted(movie.getReleaseDateInTheater()));
        } else
            holder.dateTextView.setVisibility(View.GONE);

        if (movie.getCast() != null && movie.getCast().size() > 0) {
            holder.castTextView.setVisibility(View.VISIBLE);
            holder.castTextView.setText(mContext.getString(R.string.movie_view_fragment_cast) + " " + GeneralUtils.stringToShowFromStringList(movie.getCast()));
        } else
            holder.castTextView.setVisibility(View.GONE);

        if (movie.getRuntime() > 0) {
            holder.runtimeTextView.setVisibility(View.VISIBLE);
            holder.runtimeTextView.setText(movie.getRuntime() + " " + mContext.getString(R.string.movies_row_minutes));
        } else
            holder.runtimeTextView.setVisibility(View.GONE);

        holder.ratingRatingBar.setStepSize(0.1f);
        holder.ratingRatingBar.setMax(5);
        holder.ratingRatingBar.setRating((float) (movie.getRatingAudience() / 20));
        Picasso.with(mContext).load(movie.getPoster()).into(holder.posterImageView);

        return rowView;
    }

    private String getDateFormatted(String dateString) {
        String result = dateString;

        SimpleDateFormat receiveDT = new SimpleDateFormat("yyyy-M-dd");
        SimpleDateFormat showDT = new SimpleDateFormat("MMM yyyy");
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

    private class ViewHolder {
        // each data item is just a string in this case
        public TextView titleTextView;
        public ImageView posterImageView;
        public TextView dateTextView;
        public TextView castTextView;
        public TextView runtimeTextView;
        public RatingBar ratingRatingBar;
    }
}
