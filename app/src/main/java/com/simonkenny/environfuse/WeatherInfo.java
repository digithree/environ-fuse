package com.simonkenny.environfuse;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by simonkenny on 08/03/15.
 */
public class WeatherInfo {

    // Constants
    private static final float RAINFALL_3HR_MAX = 45; //mm
    private static final float RAINFALL_3HR_MIN = 0; //mm

    // Values, normalised
    private final float daylight;
    private final float temp;
    private final float humidity;
    private final float windSpeed;
    private final float cloudiness;
    private final float sunniness;
    private final float rainfall;
    private final float snowfall;
    private final float windGust;
    private final float windDirection; // in degrees, normalised

    protected WeatherInfo(float daylight, float temp, float humidity, float windSpeed, float cloudiness,
                       float sunniness, float rainfall, float snowfall, float windGust, float windDirection) {
        this.daylight = daylight;
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.cloudiness = cloudiness;
        this.sunniness = sunniness;
        this.rainfall = rainfall;
        this.snowfall = snowfall;
        this.windGust = windGust;
        this.windDirection = windDirection;
    }

    public static WeatherInfo makeFromJSON(JSONObject jsonObj) {
        // TODO : actually use weather info json
        if( jsonObj != null ) {
            // daylight
            float _daylight = 0.f;
            try {
                JSONObject _sys = jsonObj.getJSONObject("sys");
                Date now = new Date();
                _daylight = Utils.reverseMapRange(
                        (float) (_sys.getLong("sunrise")*1000),  //correct time number for Java Date() class unix time
                        (float) (_sys.getLong("sunset")*1000),
                        (float) now.getTime()
                );
                Log.d("WeatherInfo","sunrise: "+(_sys.getLong("sunrise")*1000)
                        +", sunset: "+(_sys.getLong("sunset")*1000)+", now: "+now.getTime());
                Log.d("WeatherInfo", "daylight norm: " + _daylight);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // sunniness and cloudiness
            float _sunniness = 0.f;
            float _cloudiness = 0.f;
            try {
                JSONObject _clouds = jsonObj.getJSONObject("clouds");
                _sunniness = 1.f - ((float)_clouds.getInt("all") / 100.f);
                _cloudiness = ((float)_clouds.getInt("all") / 100.f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // rainfall
            float _rainfall = 0.f;
            try {
                JSONObject _rainfall_3hrs = jsonObj.getJSONObject("rain");
                _rainfall = Utils.reverseMapRange(RAINFALL_3HR_MIN, RAINFALL_3HR_MAX,
                        _rainfall_3hrs.getInt("3h"));
                // log scale
                _rainfall = Utils.logScale(_rainfall );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new WeatherInfo(_daylight, 0.5f, 0.5f, 0.5f, _cloudiness, _sunniness, _rainfall, 0.5f, 0.5f, 0.5f);
        }
        return new WeatherInfo(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    public float getDaylight() {
        return daylight;
    }

    public float getTemp() {
        return temp;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getCloudiness() {
        return cloudiness;
    }

    public float getSunniness() {
        return sunniness;
    }

    public float getRainfall() {
        return rainfall;
    }

    public float getSnowfall() {
        return snowfall;
    }

    public float getWindGust() {
        return windGust;
    }

    public float getWindDirection() {
        return windDirection;
    }
}
