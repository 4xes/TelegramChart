package com.telegram.chart;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.theme.ThemePreferences;

abstract class ThemeBaseActivity extends AppCompatActivity implements Themable<Theme> {

    private Theme theme;
    private ThemePreferences themePreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themePreferences = new ThemePreferences(getApplicationContext());
        theme = Theme.createTheme(this, isNightMode() ? Theme.NIGHT : Theme.DAY);
    }

    public boolean isNightMode() {
        return themePreferences.getMode() == Theme.NIGHT;
    }

    public Theme getCurrentTheme() {
        return theme;
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setStatusBarColor(theme.getPrimaryDarkColor());
        setToolbarColor(theme.getPrimaryColor());
        setWindowBackground(theme.getBackgroundWindowColor());
    }

    private void setStatusBarColor(int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    private void setToolbarColor(int toolbarColor) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(toolbarColor));
        }
    }

    private void setWindowBackground(int windowBackgroundColor) {
        getWindow().getDecorView().setBackgroundColor(windowBackgroundColor);
    }

    protected void toggleNightMode() {
        if (isNightMode()) {
            themePreferences.setMode(Theme.DAY);
            theme = Theme.createTheme(this, Theme.DAY);
            applyTheme(theme);
        } else {
            themePreferences.setMode(Theme.NIGHT);
            theme = Theme.createTheme(this, Theme.NIGHT);
            applyTheme(theme);
        }
    }

}