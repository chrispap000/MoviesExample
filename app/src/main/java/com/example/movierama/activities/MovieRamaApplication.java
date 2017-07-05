package com.example.movierama.activities;

import android.app.Application;

import com.example.movierama.webservices.RestClient;

public class MovieRamaApplication extends Application {

    public static MovieRamaApplication getInstance() {
        return mSingleton;
    }

    private static MovieRamaApplication mSingleton;

    @Override
    public void onCreate() {
        //MultiDex.install(this);
        super.onCreate();

        // Create singleton object
        mSingleton = this;

        //Initiate application's Rest Client
        new RestClient(this);
    }

}