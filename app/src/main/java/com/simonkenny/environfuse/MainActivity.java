package com.simonkenny.environfuse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
public class MainActivity extends ActionBarActivity {

    // Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private View drawerView;

    // Timer
    private final int POLL_WEATHER_WAIT = 10000;
    private boolean timerActive = true;
    private int secondCount = 0;
    private boolean waitingForWeatherData = false;


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

        // get first weather data request
        new HttpInterface.HttpAsyncTaskGetWeather()
                .execute("http://api.openweathermap.org/data/2.5/weather?q=Galway,ie");
        waitingForWeatherData = true;
        Log.d("MainActivity","Requested first weather data");
    }

    public void redrawCanvas() {
        try {
            ((TextView)findViewById(R.id.text_debug)).setText("City: "+AppSupport.getInstance().getWeather().getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    if( waitingForWeatherData ) {
                        Log.d("MainActivity","Waiting for weather data: "+secondCount);
                        if( AppSupport.getInstance().isWeatherChanged() ) {
                            Log.d("MainActivity","Got weather data");
                            redrawCanvas();
                            waitingForWeatherData = false;
                        }
                    }
                    if( secondCount >= POLL_WEATHER_WAIT ) {
                        // get new weather
                        new HttpInterface.HttpAsyncTaskGetWeather()
                                .execute("http://api.openweathermap.org/data/2.5/weather?q=Galway,ie");
                        waitingForWeatherData = true;
                        secondCount = 0;
                        ((TextView)findViewById(R.id.text_debug)).setText("Waiting for new weather data... ");
                    }
                    startAlarm();
                } else {
                    ((TextView)findViewById(R.id.text_debug)).setText("Timer deactivated: "+secondCount);
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
}
