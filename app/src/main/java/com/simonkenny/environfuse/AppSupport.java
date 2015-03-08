package com.simonkenny.environfuse;

import android.support.v7.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by simonkenny on 08/03/15.
 */
public class AppSupport {
    private final static AppSupport INSTANCE = new AppSupport();

    protected AppSupport() {
        // nothing
    } // Obstruct instantiation

    public static AppSupport getInstance() {
        return INSTANCE;
    }

    // ---- Data
    private JSONObject weather;
    private int weatherRequestNum = 0;

    public JSONObject getWeather() {
        return weather;
    }

    public void setWeather(JSONObject weather) {
        this.weather = weather;
        weatherRequestNum++;
    }

    public int getWeatherRequestNum() {
        return weatherRequestNum;
    }
}
