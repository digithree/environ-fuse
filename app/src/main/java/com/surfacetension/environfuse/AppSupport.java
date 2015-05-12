package com.surfacetension.environfuse;

import android.support.v7.widget.RecyclerView;

import com.surfacetension.environfuse.gfxParticles.ParticlesContainer;

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
    private WeatherInfo weatherInfo = new WeatherInfo();

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    private ParticlesContainer particlesContainer = new ParticlesContainer();

    public ParticlesContainer getParticlesContainer() {
        return particlesContainer;
    }
}
