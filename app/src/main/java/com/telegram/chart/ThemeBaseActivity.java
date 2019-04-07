package com.telegram.chart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
        setStatusBarColor(theme);
        setToolbarColors(theme);
    }

    private void setStatusBarColor(Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (theme.getToolbarColor() == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (theme.getId() == Theme.DAY) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        getWindow().getDecorView().setSystemUiVisibility(0);
                    }
                }
            }
            window.setStatusBarColor(theme.getToolbarColor());
        }
    }

    private void setToolbarColors(Theme theme) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackground(new ColorDrawable(theme.getToolbarColor()));
            toolbar.setTitleTextColor(theme.getTitleColor());
        }
//        ActionBar actionBar = getActionBar();
//        if (actionBar != null) {
//            actionBar.setBackgroundDrawable(new ColorDrawable(theme.getToolbarColor()));
//        }

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