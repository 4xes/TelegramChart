package com.telegram.chart;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;
import android.view.WindowManager;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;

abstract class ThemeBaseActivity extends AppCompatActivity implements Themable<Theme> {

    public boolean isNightMode() {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    }

    @Override
    public void applyTheme(Theme theme) {
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
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

}