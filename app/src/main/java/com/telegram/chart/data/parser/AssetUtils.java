package com.telegram.chart.data.parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class AssetUtils {
    public static JSONArray readJsonArray(Context context, String path) throws Throwable {
        return new JSONArray(readString(context, path));
    }

    public static JSONObject readJsonObject(Context context, String path) throws Throwable {
        return new JSONObject(readString(context, path));
    }

    private static String readString(Context context, String path) throws Throwable {
        InputStream is = null;
        String string;
        try {
            is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            string = new String(buffer, "UTF-8");
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return string;
    }

}