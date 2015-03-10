package com.simonkenny.environfuse;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simonkenny on 08/03/15.
 */
public class WeatherInfo {

    // Constants
    private static final float RAINFALL_1HR_MAX = 15; //mm
    private static final float RAINFALL_1HR_MIN = 0;  //mm
    private static final float TEMP_RANGE_EITHER = 45.f;
    private static final float TEMP_ADJUST_FOR_CELCIUS = 273.15f;
    private static final float WIND_SPEED_MAX = 30.f;
    private static final float WIND_SPEED_MIN = 0.f;
    private static final float SNOW_FALL_MAX = 7.f;
    private static final float SNOW_FALL_MIN = 0.f;

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

    private final Date sunrise_;
    private final Date sunset_;
    private final float temp_;
    private final float humidity_;
    private final float windspeed_;
    private final float cloudiness_;
    private final float sunniness_;
    private final float rainfall_;
    private final float snowfall_;
    private final float windGust_;
    private final float windDirection_;

    public WeatherInfo(float daylight, float temp, float humidity, float windSpeed,
                       float cloudiness, float sunniness, float rainfall, float snowfall,
                       float windGust, float windDirection, Date sunrise_, Date sunset_,
                       float temp_, float humidity_, float windspeed_, float cloudiness_,
                       float sunniness_, float rainfall_, float snowfall_, float windGust_,
                       float windDirection_) {
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
        this.sunrise_ = sunrise_;
        this.sunset_ = sunset_;
        this.temp_ = temp_;
        this.humidity_ = humidity_;
        this.windspeed_ = windspeed_;
        this.cloudiness_ = cloudiness_;
        this.sunniness_ = sunniness_;
        this.rainfall_ = rainfall_;
        this.snowfall_ = snowfall_;
        this.windGust_ = windGust_;
        this.windDirection_ = windDirection_;
    }

    public static WeatherInfo makeFromJSON(JSONObject jsonObj) {
        if( jsonObj != null ) {
            // daylight
            float _daylight = 0.f;
            Date _sunrise_ = new Date();
            Date _sunset_ = new Date();
            try {
                JSONObject _sys = jsonObj.getJSONObject("sys");
                Date now = new Date();
                _sunrise_ = new Date(_sys.getLong("sunrise")*1000);
                _sunset_ = new Date(_sys.getLong("sunset")*1000);
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
            float _sunniness_ = 0.f;
            float _cloudiness_ = 0.f;
            try {
                JSONObject _clouds = jsonObj.getJSONObject("clouds");
                _sunniness = 1.f - ((float)_clouds.getInt("all") / 100.f);
                _cloudiness = ((float)_clouds.getInt("all") / 100.f);
                _sunniness_ = 100.f - (float)_clouds.getInt("all");
                _cloudiness_ = (float)_clouds.getInt("all");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // rainfall
            float _rainfall = -1.f;
            float _rainfall_ = 0.f;
            try {
                JSONObject _rainfall_3hrs = jsonObj.getJSONObject("rain");
                _rainfall = Utils.reverseMapRange(RAINFALL_1HR_MIN*3, RAINFALL_1HR_MAX*3,
                        _rainfall_3hrs.getInt("3h"));
                _rainfall_ = (float)_rainfall_3hrs.getDouble("3h");
                // log scale
                _rainfall = Utils.logScale(_rainfall );
            } catch (JSONException e) {
                // do nothing
                Log.d("WeatherInfo","Can't get rain:3h");
            }
            if( _rainfall == -1.f ) {
                try {
                    JSONObject _rainfall_3hrs = jsonObj.getJSONObject("rain");
                    _rainfall = Utils.reverseMapRange(RAINFALL_1HR_MIN, RAINFALL_1HR_MAX,
                            _rainfall_3hrs.getInt("1h"));
                    _rainfall_ = (float)_rainfall_3hrs.getDouble("1h");
                    // log scale
                    _rainfall = Utils.logScale(_rainfall );
                } catch (JSONException e) {
                    // do nothing
                    Log.d("WeatherInfo","Can't get rain:1h");
                }
            }
            // temp, humidity
            float _temp = 0.f;
            float _temp_ = 0.f;
            float _humidity = 0.f;
            float _humidity_ = 0.f;
            try {
                JSONObject _main = jsonObj.getJSONObject("main");
                // temp
                _temp = Utils.reverseMapRange(-TEMP_RANGE_EITHER, TEMP_RANGE_EITHER,
                        (float)_main.getDouble("temp")-TEMP_ADJUST_FOR_CELCIUS);
                _temp_ = (float)_main.getDouble("temp")-TEMP_ADJUST_FOR_CELCIUS;
                // humidity
                _humidity = (float)_main.getInt("humidity") / 100.f;
                _humidity = _humidity * _humidity;
                _humidity_ = (float)_main.getInt("humidity");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // wind: speed, direction
            float _windspeed = 0.f;
            float _windspeed_ = 0.f;
            float _winddir = 0.f;
            float _winddir_ = 0.f;
            try {
                JSONObject _wind = jsonObj.getJSONObject("wind");
                // speed
                _windspeed = Utils.reverseMapRange(WIND_SPEED_MIN, WIND_SPEED_MAX,
                        (float)_wind.getDouble("speed"));
                _windspeed_ = (float)_wind.getDouble("speed");
                _windspeed = Utils.logScale(_windspeed ); // log scale
                // direction
                _winddir = (float)_wind.getInt("deg");
                _winddir_ = (float)_wind.getInt("deg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // snowfall
            float _snowfall = -1.f;
            float _snowfall_ = 0.f;
            try {
                JSONObject _snow = jsonObj.getJSONObject("snow");
                _snowfall = Utils.reverseMapRange(SNOW_FALL_MIN*3, SNOW_FALL_MAX*3,
                        (float)_snow.getDouble("3h"));
                _snowfall_ = (float)_snow.getDouble("3h");
                // log scale
                _snowfall = Utils.logScale(_snowfall );
            } catch (JSONException e) {
                // do nothing
                Log.d("WeatherInfo","Can't get snow:3h");
            }
            if( _snowfall == -1.f ) {
                try {
                    JSONObject _snow = jsonObj.getJSONObject("snow");
                    _snowfall = Utils.reverseMapRange(SNOW_FALL_MIN*3, SNOW_FALL_MAX*3,
                            (float)_snow.getDouble("1h"));
                    _snowfall_ = (float)_snow.getDouble("1h");
                    // log scale
                    _snowfall = Utils.logScale(_snowfall );
                } catch (JSONException e) {
                    // do nothing
                    Log.d("WeatherInfo","Can't get snow:1h");
                }
            }
            // return new WearherInfo object
            return new WeatherInfo(_daylight, _temp, _humidity, _windspeed, _cloudiness, _sunniness,
                    _rainfall, _snowfall, 0.5f, _winddir,
                    _sunrise_, _sunset_, _temp_, _humidity_, _windspeed_, _cloudiness_, _sunniness_,
                    _rainfall_, _snowfall_, 0.f, _winddir_);
        }
        return new WeatherInfo(0.5f, 0.5f, 1.f, 1.f, 0.5f, 1.f, 1.f, 0.5f, 0.5f, 0.5f,
                new Date(), new Date(), 0.f, 0.f, 1.f, 0.f, 0.f, 0.f, 0.f, 0.f, 0.f);
        /*
        private final Date sunrise_;
    private final Date sunset_;
    private final float temp_;
    private final float humidity_;
    private final float windspeed_;
    private final float cloudiness_;
    private final float sunniness_;
    private final float rainfall_;
    private final float snowfall_;
    private final float windGust_;
    private final float windDirection_;
         */
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

    // text in with units

    public String getSunriseAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(sunrise_);
    }

    public String getSunsetAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(sunset_);
    }

    public String getTempAsString() {
        return String.format("%.1f C",temp_);
    }

    public String getHumidityAsString() {
        return String.format("%.0f%%", humidity_);
    }

    public String getWindspeedAsString() {
        return String.format("%.1f mps", windspeed_);
    }

    public String getCloudinessAsString() {
        return String.format("%.0f%%", cloudiness_);
    }

    public String getSunninessAsString() {
        return String.format("%.0f%%", sunniness_);
    }

    public String getRainfallAsString() {
        return String.format("%.1f mm p/h", rainfall_);
    }

    public String getSnowfallAsString() {
        return String.format("%.1f mm p/h", snowfall_);
    }

    public String getWindGustAsString() {
        return String.format("%.1f mps", windGust_);
    }

    public String getWindDirectionAsString() {
        return String.format("%.0f deg", windDirection_);
    }
}
