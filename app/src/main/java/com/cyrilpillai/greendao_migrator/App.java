package com.cyrilpillai.greendao_migrator;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;


public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(context);
        }
    }

    public static Context getContext() {
        return context;
    }
}
