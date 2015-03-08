package com.simonkenny.environfuse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by simonkenny on 22/01/15.
 */
public class MainActivity extends ActionBarActivity implements LocationListener {

    // Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View drawerView;

    // Timer
    private final int POLL_WEATHER_WAIT = 60000;
    private boolean timerActive = true;
    private int secondCount = 0;
    private boolean waitingForWeatherData = false;

    // Location
    private LocationManager locationManager = null;
    private Location currentBestLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.drawer_container, new DrawerFragment())
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);

        drawerView = (View)findViewById(R.id.drawer_container);

        // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.primarydark));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,toolbar, R.string.app_name, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle("Keen Time");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle("Drawer");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().setStatusBarColor(getResources().getColor(R.color.primary));  //DOES NOTHING
            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
        }
        */

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        GlobalSettings.getInstance().setSharedPreferences(sharedPreferences);

        // location
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    public void redrawCanvas() {
        try {
            ((TextView)findViewById(R.id.text_title)).setText(AppSupport.getInstance().getWeather().getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateWeather() {
        Location location = getLastBestLocation();
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        ((TextView)findViewById(R.id.text_title)).setText(getString(R.string.main_fragment_text_wait_weather));
        // get first weather data request
        new HttpInterface.HttpAsyncTaskGetWeather()
                .execute("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon);
        waitingForWeatherData = true;
        Log.d("MainActivity","updateWeather: lat="+lat+", lon="+lon);
    }

    public void startAlarm() {
        timerActive  = true;
        // set up alarm
        new CountDownTimer(1000, 100) { //make tick small, might crash it though?
            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                secondCount++;
                if( timerActive ) {
                    if( currentBestLocation == null ) {
                        Log.d("MainActivity","currentBestLocation null, updating weather with location request");
                        updateWeather();
                    }
                    if( waitingForWeatherData ) {
                        Log.d("MainActivity","Waiting for weather data: "+secondCount);
                        if( AppSupport.getInstance().isWeatherChanged() ) {
                            Log.d("MainActivity","Got weather data");
                            redrawCanvas();
                            waitingForWeatherData = false;
                        }
                    }
                    if( secondCount >= POLL_WEATHER_WAIT ) {
                        updateWeather();
                        secondCount = 0;
                    }
                    startAlarm();
                } else {
                    Toast.makeText(getApplicationContext(),"Timer deactivated",Toast.LENGTH_SHORT).show();
                    Log.d("MainActivity","Timer is disabled");
                }
            }
        }.start();
    }

    public void cancelAlarm() {
        timerActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity","onResume: startAlarm");
        secondCount = 0;
        startAlarm();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity","Destroy: cancelAlarm");
        cancelAlarm();
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawerView);
        //menu.findItem(R.id.).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Fragment in main panel of app
     */
    public static class MainFragment extends Fragment {

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);

            final Context mContext = this.getActivity();

            // TODO : build view

            return rootView;
        }
    }

    /**
     * Fragment in main panel of app
     */
    public static class DrawerFragment extends Fragment {

        public DrawerFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.drawer_fragment, container, false);

            final Context mContext = this.getActivity();

            // TODO : build view

            return rootView;
        }
    }


    // LOCATION services

    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }


    public void onLocationChanged(Location location) {
        Log.d("MainActivity","onLocationChanged");
        makeUseOfNewLocation(location);

        if(currentBestLocation == null) {
            currentBestLocation = location;
        }

        // do something with current best location
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    void makeUseOfNewLocation(Location location) {
        if ( isBetterLocation(location, currentBestLocation) ) {
            currentBestLocation = location;
        }
    }

    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 120000;
        boolean isSignificantlyOlder = timeDelta < - 120000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
