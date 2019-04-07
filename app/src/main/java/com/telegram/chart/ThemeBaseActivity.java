package com.telegram.chart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.theme.ThemePreferences;

abstract class ThemeBaseActivity extends Activity implements Themable {

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackground(new ColorDrawable(toolbarColor));
        }
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