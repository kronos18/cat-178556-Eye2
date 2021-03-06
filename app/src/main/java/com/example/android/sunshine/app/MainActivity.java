/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.sync.LocalisationGPSListener;
import com.example.android.sunshine.app.sync.SyncAdapter;

public class MainActivity extends ActionBarActivity implements RestaurantFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    private LocationManager locationManager;
    private LocalisationGPSListener localisationGPSListener;
    private int temps = 5000; // milliseconds
    private int distance = 10; // meters
    public RestaurantFragment restaurantFragment;
    final Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferenceRayon(this);

        setContentView(R.layout.activity_main);




        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        this.restaurantFragment =  ((RestaurantFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));

        SyncAdapter.initialisationDuSyncAdapter(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_map)
        {
            startActivity(new Intent(this, MapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferenceRayon(this);
        // update the location in our second pane using the fragment manager
            if (location != null && !location.equals(mLocation)) {
            RestaurantFragment ff = (RestaurantFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    private void miseEnPlaceGeolocalisation() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //*************ecouteur ou listener*********************
        localisationGPSListener = new LocalisationGPSListener();

        //on met a jour toute les 5 secondes et qu'on est bouge de 10m avant de recuperer les coordonnees a nouveau
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                temps,
                distance,
                localisationGPSListener);

    }

    public void refresh() {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                restaurantFragment.refreshData();
            }
        });
    }


    /*
    *****************AZADEH********************
     */
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//
//        Uri restaurantUri = RestaurantContract.RestaurantEntry.CONTENT_URI;
//        System.out.println("l'uri est : "+restaurantUri);
//
//
//        return new CursorLoader(this,
//                restaurantUri,
//                RestaurantFragment.RESTAURANT_COLUMNS,
//                null,
//                null,
//                null);
//
//    }
//
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        //  LocationManager lm=(LocationManager) getSystemService(LOCATION_SERVICE);
//        //  Intent i= new Intent("com.alert.proximityalert");           //Custom Action
//        //  PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), -1, i, 0);
//
//
//        data.moveToFirst();
//        //   i.putExtra("name", data.getString(1));
//        //   i.putExtra("address", data.getString(2));
//        createNotification(data.getString(1), data.getString(2),R.mipmap.ic_launcher);
//
//        // float radius = 30000;
//      /* while (data.moveToNext()) {
//            Toast.makeText(this, "lon  = "+data.getInt(8)+" - "+"lat  = "+data.getInt(9), Toast.LENGTH_SHORT).show();
//            double lat=data.getInt(9);
//           double lon=data.getInt(8);
//           lm.addProximityAlert(lat,lon,radius, -1, pi);
//
//        }*/
//
//
//        // this.sendBroadcast(i);
//
//
//
//        //lm.addProximityAlert(45.1936541,5.7684217,radius, -1, pi);
//
//    }
//
//
//
//
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
//    //*********************************************************************
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public void createNotification(String name, String address,int icon) {
//        // Prepare intent which is triggered if the
//        // notification is selected
//
//        Intent intent = new Intent(this,this.getClass());
//        PendingIntent pIntent = PendingIntent.getActivity(this,0, intent, 0);
//
//        // Build notification
//        // Actions are just fake
//        Notification noti = new Notification.Builder(this)
//                .setContentTitle(name)
//                .setContentText(address).setSmallIcon(icon)
//                .setContentIntent(pIntent).build();
//
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        // hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, noti);
////****************************************************************************
//    }
}
