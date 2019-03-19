package com.community.jboss.leadmanagement;

import android.app.Application;

import timber.log.Timber;

public class LeadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
