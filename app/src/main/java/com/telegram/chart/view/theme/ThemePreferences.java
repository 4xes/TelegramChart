package com.telegram.chart.view.theme;

import android.content.Context;
import android.content.SharedPreferences;

import com.telegram.chart.BuildConfig;

public class ThemePreferences {

    private final SharedPreferences preferences;

    public ThemePreferences(Context context) {
        this.preferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    public @Theme.ThemeId int getMode() {
        return this.preferences.getInt(KEY_ID, Theme.DAY);
    }

    public void setMode(@Theme.ThemeId int themeId) {
        this.preferences.edit().putInt(KEY_ID, themeId).apply();
    }

    private static final String KEY = "theme_";
    private static final String KEY_ID = KEY + "id";
}
