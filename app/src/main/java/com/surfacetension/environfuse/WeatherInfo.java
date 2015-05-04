package com.surfacetension.environfuse;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simonkenny on 08/03/15.
 */
public class WeatherInfo {

    // Weather APIs
    public static final int API_OPEN_WEATHER_MAP = 0;
    public static final int API_WUNDERGROUND = 1;

    // Constants
    private static final float RAINFALL_1HR_MAX = 15; //mm
    private static final float RAINFALL_1HR_MIN = 0;  //mm
    private static final float TEMP_RANGE_EITHER = 45.f;
    private static final float TEMP_ADJUST_FOR_CELCIUS = 273.15f;
    private static final float WIND_SPEED_MAX = 30.f;
    private static final float WIND_SPEED_MIN = 0.f;
    private static final float SNOW_FALL_MAX = 7.f;
    private static final float SNOW_FALL_MIN = 0.f;

    // General
    private String locationName;
    // Stats
    private Date created;
    // Values, normalised
    private float daylight;
    private float temp;
    private float humidity;
    private float windSpeed;
    private float cloudiness;
    private float sunniness;
    private float rainfall;
    private float snowfall;
    private float windGust;
    private float windDirection; // in degrees, normalised

    private Date sunrise_native;
    private Date sunset_native;
    private float temp_native;
    private float humidity_native;
    private float windspeed_native;
    private float cloudiness_native;
    private float sunniness_native;
    private float rainfall_native;
    private float snowfall_native;
    private float windGust_native;
    private float windDirection_native;

    private boolean dataUpdated = false;


    public WeatherInfo() {
        created = new Date();
        sunrise_native = new Date();
        sunset_native = new Date();
    }

    public boolean getDataUpdated() {
        boolean ret = dataUpdated;
        dataUpdated = false;
        return ret;
    }

    private boolean addWeatherApiJSON_Wunderground(JSONObject jsonObj) {
        if( jsonObj != null ) {
            try {
                JSONObject _main = jsonObj.getJSONObject("current_observation");
                String _name = "[getting name]";
                try {
                    _name = _main.getJSONObject("display_location").getString("full");
                } catch (JSONException e) {
                    _name = "[can't get name]";
                    Log.d("WeatherInfo", "Can't get location name");
                }
                // daylight : no information from Wunderground
                float _daylight = 0.5f;
                /*
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
                */
                /*
                // sunniness and cloudiness
                float _sunniness = 0.f;
                float _cloudiness = 0.f;
                float _sunniness_ = 0.f;
                float _cloudiness_ = 0.f;
                try {
                    JSONObject _clouds = jsonObj.getJSONObject("clouds");
                    _sunniness = 1.f - ((float) _clouds.getInt("all") / 100.f);
                    _cloudiness = ((float) _clouds.getInt("all") / 100.f);
                    _sunniness_ = 100.f - (float) _clouds.getInt("all");
                    _cloudiness_ = (float) _clouds.getInt("all");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                // rainfall
                float _rainfall = 0.f;
                float _rainfall_ = 0.f;
                try {

                    //precip_1hr_metric
                    _rainfall_ = (float)_main.getDouble("precip_1hr_metric");
                    _rainfall = Utils.reverseMapRange(RAINFALL_1HR_MIN, RAINFALL_1HR_MAX,
                            _rainfall_);
                    // log scale
                    _rainfall = Utils.logScale(_rainfall);
                } catch (JSONException e) {
                    // do nothing
                    Log.d("WeatherInfo", "Can't get rain");
                }
                // temp
                float _temp = 0.f;
                float _temp_ = 0.f;
                try {
                    // temp
                    _temp_ = (float) _main.getDouble("temp_c");
                    _temp = Utils.reverseMapRange(-TEMP_RANGE_EITHER, TEMP_RANGE_EITHER,_temp_);
                } catch (JSONException e) {
                    Log.d("WeatherInfo", "Can't get temp");
                }
                //humidity
                float _humidity = 0.f;
                float _humidity_ = 0.f;
                try {
                    // humidity
                    String humidityStr = _main.getString("relative_humidity");
                    _humidity_ = Float.parseFloat(humidityStr.split("[%]")[0]);
                    _humidity = _humidity_ / 100.f;
                    _humidity = _humidity * _humidity;
                } catch (JSONException e) {
                    Log.d("WeatherInfo", "Can't get humidity");
                }
                // wind: speed, direction
                float _windspeed = 0.f;
                float _windspeed_ = 0.f;
                float _winddir = 0.f;
                float _winddir_ = 0.f;
                try {
                    // speed
                    _windspeed_ = (float) _main.getDouble("wind_kph");
                    _windspeed = Utils.reverseMapRange(WIND_SPEED_MIN, WIND_SPEED_MAX,
                            _windspeed_);
                    _windspeed = Utils.logScale(_windspeed); // log scale
                    // direction
                    _winddir_ = (float) _main.getInt("wind_degrees");
                    _winddir = _winddir_;
                } catch (JSONException e) {
                    Log.d("WeatherInfo", "Can't get wind");
                }
                // snowfall
                float _snowfall = 0.f;
                float _snowfall_ = 0.f;
                /*
                try {
                    JSONObject _snow = jsonObj.getJSONObject("snow");
                    _snowfall = Utils.reverseMapRange(SNOW_FALL_MIN * 3, SNOW_FALL_MAX * 3,
                            (float) _snow.getDouble("3h"));
                    _snowfall_ = (float) _snow.getDouble("3h");
                    // log scale
                    _snowfall = Utils.logScale(_snowfall);
                } catch (JSONException e) {
                    // do nothing
                    Log.d("WeatherInfo", "Can't get snow:3h");
                }
                if (_snowfall == -1.f) {
                    try {
                        JSONObject _snow = jsonObj.getJSONObject("snow");
                        _snowfall = Utils.reverseMapRange(SNOW_FALL_MIN * 3, SNOW_FALL_MAX * 3,
                                (float) _snow.getDouble("1h"));
                        _snowfall_ = (float) _snow.getDouble("1h");
                        // log scale
                        _snowfall = Utils.logScale(_snowfall);
                    } catch (JSONException e) {
                        // do nothing
                        _snowfall = 0.f;
                        Log.d("WeatherInfo", "Can't get snow:1h");
                    }
                }
                */

                // update info
                //locationName = _name;
                //daylight = selectBestPrecision(daylight, _daylight);
                temp = selectBestPrecision(temp, _temp);
                humidity = selectBestPrecision(humidity, _humidity);
                windSpeed = selectBestPrecision(windSpeed, _windspeed);
                rainfall = selectBestPrecision(rainfall, _rainfall);
                snowfall = selectBestPrecision(snowfall, _snowfall);
                windDirection = selectBestPrecision(windDirection, _winddir);
                //new Date(), new Date() ??
                temp_native = selectBestPrecision(temp_native, _temp_);
                humidity_native = selectBestPrecision(humidity_native, _humidity_);
                windspeed_native = selectBestPrecision(windspeed_native, _windspeed_);
                rainfall_native = selectBestPrecision(rainfall_native, _rainfall_);
                snowfall_native = selectBestPrecision(snowfall_native, _snowfall_);
                windDirection_native = selectBestPrecision(windDirection_native, _winddir_);
                // set flag for update
                dataUpdated = true;
            } catch (JSONException e) {
                Log.d("WeatherInfo", "Couldn't parse main entry current_observation");
            }
        }
        return true;
    }

    private float selectBestPrecision( float oldVal, float newVal ) {
        if( oldVal == 0.f && newVal != 0.f ) {
            return newVal;
        }
        if( oldVal != 0.f && newVal == 0.f ) {
            return oldVal;
        }
        return newVal;
    }

    private boolean addWeatherApiJSON_OpenWeatherMap(JSONObject jsonObj) {
        if( jsonObj != null ) {
            String _name = "[getting name]";
            try {
                _name = jsonObj.getString("name");
            } catch (JSONException e) {
                _name = "[can't get name]";
            }
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
                    _rainfall = 0.f;
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
                    _snowfall = 0.f;
                    Log.d("WeatherInfo","Can't get snow:1h");
                }
            }
            // update
            locationName = _name;

            daylight = selectBestPrecision(daylight, _daylight);
            temp = selectBestPrecision(temp, _temp);
            humidity = selectBestPrecision(humidity, _humidity);
            windSpeed = selectBestPrecision(windSpeed, _windspeed);
            cloudiness = selectBestPrecision(cloudiness, _cloudiness);
            sunniness = selectBestPrecision(sunniness, _sunniness);
            rainfall = selectBestPrecision(rainfall, _rainfall);
            snowfall = selectBestPrecision(snowfall, _snowfall);
            windDirection = selectBestPrecision(windDirection, _winddir);
            // native
            sunrise_native = _sunrise_;
            sunset_native = _sunset_;
            temp_native = selectBestPrecision(temp_native, _temp_);
            humidity_native = selectBestPrecision(humidity_native, _humidity_);
            windspeed_native = selectBestPrecision(windspeed_native, _windspeed_);
            cloudiness_native = selectBestPrecision(cloudiness_native, _cloudiness_);
            sunniness_native = selectBestPrecision(sunniness_native, _sunniness_);
            rainfall_native = selectBestPrecision(rainfall_native, _rainfall_);
            snowfall_native = selectBestPrecision(snowfall_native, _snowfall_);
            windDirection_native = selectBestPrecision(windDirection_native, _winddir_);
            // set flag for update
            dataUpdated = true;
        }
        return true;
    }

    // accessors
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
        return sdf.format(sunrise_native);
    }

    public String getSunsetAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(sunset_native);
    }

    public String getTempAsString() {
        return String.format("%.1f C", temp_native);
    }

    public String getHumidityAsString() {
        return String.format("%.0f%%", humidity_native);
    }

    public String getWindspeedAsString() {
        return String.format("%.1f mps", windspeed_native);
    }

    public String getCloudinessAsString() {
        return String.format("%.0f%%", cloudiness_native);
    }

    public String getSunninessAsString() {
        return String.format("%.0f%%", sunniness_native);
    }

    public String getRainfallAsString() {
        return String.format("%.1f mm p/h", rainfall_native);
    }

    public String getSnowfallAsString() {
        return String.format("%.1f mm p/h", snowfall_native);
    }

    public String getWindGustAsString() {
        return String.format("%.1f mps", windGust_native);
    }

    public String getWindDirectionAsString() {
        return String.format("%.0f deg", windDirection_native);
    }

    public String getLastUpdateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(created);
    }

    public String getLocationName() {
        return locationName;
    }


    // --- Internet tasks

    public void getFromWeatherAPI(double lat, double lon, String locationName) {
        new HttpAsyncTaskGetWeather_OpenWeatherMap()
                .execute("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon);
        new HttpAsyncTaskGetWeather_Wunderground()
                .execute("http://api.wunderground.com/api/e83ed79e8184660a/conditions/q/ie/"+locationName+".json");
    }

    private static class HttpAsyncTaskGetWeather_OpenWeatherMap extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("GetWeather_OWM","Sending HTTP GET request");
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("GetWeather_OWM","Got weather data from API");
            Log.d("GetWeather_OWM","Data: "+result);
            // convert JSON string data to JSON object
            try {
                JSONObject json = new JSONObject(result);
                AppSupport.getInstance().getWeatherInfo().addWeatherApiJSON_OpenWeatherMap(json);
            } catch (JSONException e) {
                Log.d("GetWeather_OWM","JSON parse error!");
                e.printStackTrace();
            }
        }
    }

    private static class HttpAsyncTaskGetWeather_Wunderground extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Log.d("GetWeather_Wunderground","Sending HTTP GET request");
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("GetWeather_Wunderground","Got weather data from API");
            Log.d("GetWeather_Wunderground","Data: "+result);
            // convert JSON string data to JSON object
            try {
                JSONObject json = new JSONObject(result);
                AppSupport.getInstance().getWeatherInfo().addWeatherApiJSON_Wunderground(json);
            } catch (JSONException e) {
                Log.d("GetWeather_Wunderground","JSON parse error!");
                e.printStackTrace();
            }
        }
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            Log.d("WeatherInfo","Making HTTP GET request to: "+url);
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("WeatherInfo", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
