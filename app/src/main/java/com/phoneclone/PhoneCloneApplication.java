package com.phoneclone;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class PhoneCloneApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
    }
}

