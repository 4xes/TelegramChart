package com.telegram.chart;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}