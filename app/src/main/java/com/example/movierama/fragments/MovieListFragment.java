package com.example.movierama.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.movierama.R;
import com.example.movierama.activities.MovieViewActivity;
import com.example.movierama.adapters.MoviesListAdapter;
import com.example.movierama.events.GotAllMoviesByTitleEvent;
import com.example.movierama.events.GotAllMoviesInTheaterEvent;
import com.example.movierama.helpers.SystemUtils;
import com.example.movierama.models.Movie;
import com.example.movierama.webservices.RestClient;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends MovieRamaBaseFragment {

    private enum LIST_TYPE {
        MOVIES_IN_THEATER, MOVIES_BY_NAME
    }

    //Variables
    private final int PAGE_LIMIT = 10; // Numbers of movies requested per server call
    private int mPageCount = 1;
    private boolean mLoadingItems = false;
    private int mTotalCount; // Total count of movie items for each Server call.
    private String mCountryCode;
    private boolean mGotUserLocation = false;

    MoviesListAdapter mAdapter;
    View mFooterView;

    int mCurrentVisibleItemCount, mCurrentScrollState, mCurrentFirstVisibleItem;

    LIST_TYPE currentListType = LIST_TYPE.MOVIES_IN_THEATER;

    LocationManager mLocationManager;


    //Binding Views
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.itemsListView)
    ListView mItemsListView;

    @Bind(R.id.searchEditText)
    EditText mSearchEditText;

    @Bind(R.id.noMoviesTextView)
    TextView mNoMoviesTextView;


    //Binding View Events
    @OnTextChanged(R.id.searchEditText)
    void searchEditTextChanged(CharSequence text) {
        if (mAdapter != null)
            mAdapter.clearItems();

        mPageCount = 1;
        mNoMoviesTextView.setVisibility(View.GONE);

        //Add list footer if necessary
        if (mItemsListView.getFooterViewsCount() == 0)
            mItemsListView.addFooterView(mFooterView);

        mProgressBar.setVisibility(View.VISIBLE);

        if (text.toString().equals("")) {
            currentListType = LIST_TYPE.MOVIES_IN_THEATER;
            RestClient.getInstance().getAllMoviesInTheaterRequest(PAGE_LIMIT, mPageCount, mCountryCode);
        } else {
            currentListType = LIST_TYPE.MOVIES_BY_NAME;
            RestClient.getInstance().getAllMoviesByTitleRequest(PAGE_LIMIT, mPageCount, text.toString());
        }
    }

    @OnClick(R.id.searchResetImage)
    void searchResetImageClicked() {
        mSearchEditText.setText("");

        //Close keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public MovieListFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        //ButterKnife bind for injecting the views
        ButterKnife.bind(this, view);

        //Set countryCode default value the devices configuration until location is found.
        mCountryCode = getResources().getConfiguration().locale.getCountry();

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Check if GPS is enabled
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            SystemUtils.showGPSDisabledAlertToUser(getActivity());
        }

        //Check if there is Internet
        if (SystemUtils.checkIfInternetIsAvailable(getActivity()) == false) {
            SystemUtils.showNoInternetAlertToUser(getActivity());
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }

        //Initiate and add list's footer view
        mFooterView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.movie_list_loading_more_layout, null, false);
        mItemsListView.addFooterView(mFooterView);

        mItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), MovieViewActivity.class);
                intent.putExtra("id", ((Movie) adapterView.getItemAtPosition(i)).get_id());
                startActivity(intent);
            }
        });
        mItemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                mCurrentScrollState = scrollState;
                isScrollCompleted();

                if (scrollState != 0) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mCurrentFirstVisibleItem = firstVisibleItem;
                mCurrentVisibleItemCount = visibleItemCount;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPageCount = 1;

                if (mAdapter != null)
                    mAdapter.clearItems();

                mNoMoviesTextView.setVisibility(View.GONE);

                if (mItemsListView.getFooterViewsCount() == 0)
                    mItemsListView.addFooterView(mFooterView);

                if (currentListType == LIST_TYPE.MOVIES_IN_THEATER) {
                    RestClient.getInstance().getAllMoviesInTheaterRequest(PAGE_LIMIT, mPageCount, mCountryCode);
                } else if (currentListType == LIST_TYPE.MOVIES_BY_NAME) {
                    RestClient.getInstance().getAllMoviesByTitleRequest(PAGE_LIMIT, mPageCount, mSearchEditText.getText().toString());
                }
            }
        });

        return view;
    }

    private void isScrollCompleted() {
        if (mCurrentVisibleItemCount > 0 && mCurrentScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            /*** In this way I detect if there's been a scroll which has completed ***/
            /*** do the work for load more date! ***/
            if (!mLoadingItems) {
                mLoadingItems = true;
                loadMoreItems();
            }
        }
    }

    private void loadMoreItems() {
        mPageCount++;
        mNoMoviesTextView.setVisibility(View.GONE);
        if (currentListType == LIST_TYPE.MOVIES_IN_THEATER) {
            RestClient.getInstance().getAllMoviesInTheaterRequest(PAGE_LIMIT, mPageCount, mCountryCode);
        } else if (currentListType == LIST_TYPE.MOVIES_BY_NAME) {
            RestClient.getInstance().getAllMoviesByTitleRequest(PAGE_LIMIT, mPageCount, mSearchEditText.getText().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Register EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        //Unregister EventBus
        EventBus.getDefault().unregister(this);
    }

    private void showItemsInList(List<Movie> movies) {

        if (mAdapter == null) {
            mAdapter = new MoviesListAdapter(getActivity(), movies);
            mItemsListView.setAdapter(mAdapter);

        } else {
            mAdapter.addItems(movies);
        }
        if (mAdapter.getCount() >= mTotalCount) { // OK we have loaded all existing items

            //Stop loading
            mLoadingItems = true;

            //Remove list's footer (Loading more)
            if (mFooterView != null)
                mItemsListView.removeFooterView(mFooterView);
        } else {
            mLoadingItems = false;
        }
    }


    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {


                if (mGotUserLocation == false) { // Do it only once, we just need the country that user currently is.
                    mGotUserLocation = true;
                    mCountryCode = getCountryCodeByLocation(location);

                    //Make the call if list is empty
                    if (mAdapter == null && currentListType == LIST_TYPE.MOVIES_IN_THEATER) {
                        mNoMoviesTextView.setVisibility(View.GONE);
                        RestClient.getInstance().getAllMoviesInTheaterRequest(PAGE_LIMIT, mPageCount, mCountryCode);
                    }

                    //Disable GPS listener. We do not need GPS any more.
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLocationManager.removeUpdates(mLocationListener);
                    }
                }
            }
        }
    };

    private String getCountryCodeByLocation(Location location) {
        try {
            String result = getResources().getConfiguration().locale.getCountry();

            Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
                return result;
            } else {
                if (addresses.size() > 0) {
                    result = addresses.get(0).getCountryCode();
                }
            }
            return result;
        } catch (Exception e) {

        }
        return "US";
    }

    //Event Bus Events - RestClient Event
    public void onEvent(GotAllMoviesInTheaterEvent event) {
        mProgressBar.setVisibility(View.GONE);

        if (event.getMovies() != null) {
            if (event.getMovies().size() > 0) {
                mTotalCount = event.getTotalCount();

                //If is new call (paging == 1) initiate adapter. This solves the case that
                //asychronous calls on text changed listener return results in wrong order.
                if (event.getPaging() == 1 && mAdapter != null)
                    mAdapter.clearItems();

                showItemsInList(event.getMovies());
            } else if (event.getPaging() == 1) {
                mNoMoviesTextView.setVisibility(View.VISIBLE);
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onEvent(GotAllMoviesByTitleEvent event) {
        mProgressBar.setVisibility(View.GONE);

        if (event.getMovies() != null) {
            if (event.getMovies().size() > 0) {
                mTotalCount = event.getTotalCount();

                //If is new call (paging == 1) initiate adapter. This solves the case that
                //asychronous calls on text changed listener return results in wrong order.
                if (event.getPaging() == 1 && mAdapter != null)
                    mAdapter.clearItems();

                showItemsInList(event.getMovies());
            } else if (event.getPaging() == 1) {
                mNoMoviesTextView.setVisibility(View.VISIBLE);
            }
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
